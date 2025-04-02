package com.ssafy.searchserver.application.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.searchserver.common.util.Validation;
import com.ssafy.searchserver.interfaces.search.dto.KeywordRankingData;
import com.ssafy.searchserver.interfaces.search.dto.RelatedKeywordsResponse;
import com.ssafy.searchserver.domain.search.model.ForeignNewsElastic;
import com.ssafy.searchserver.interfaces.search.dto.SentimentMentionData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final ElasticsearchClient esClient;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Map<String, CompletableFuture<?>> pendingCompareResults = new ConcurrentHashMap<>();
    @Value("${call_back_url}")
    private String callBackUrl;
    private final int timeout = 60;


    public RelatedKeywordsResponse getRelatedKeywords(String keyword, String category, int period, boolean isKorea) {
        try {
            // 유효성 검사
//            Validation.validateCategoryAndPeriod(category, period);
            long start = System.currentTimeMillis();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime from = now.minusDays(period);

            String gte = from.format(formatter);
            String lte = now.format(formatter);

            // 필터링 Query
            Query boolQuery = Query.of(q -> q.bool(b -> {
                List<Query> mustQueries = new ArrayList<>();


                mustQueries.add(Query.of(m -> m.term(t -> t
                        .field("keywords")
                        .value(FieldValue.of(keyword))
                )));
                if (!category.equalsIgnoreCase("all")) {
                    mustQueries.add(Query.of(m -> m.term(t -> t
                            .field("categories")
                            .value(FieldValue.of(category))
                    )));
                }
                mustQueries.add(Query.of(m -> m.range(r -> r
                        .date(d -> d
                                .field("published_at")
                                .gte(gte)
                                .lte(lte)
                        )
                )));
                return b.must(mustQueries);
            }));

            //  연관 키워드 추출을 위한 terms aggregation
            // keyword가 포함된 뉴스에서 다른 키워드들을 연관어로 뽑음
            Aggregation agg = Aggregation.of(a -> a
                    .terms(t -> t
                            .field("keywords.keyword") // 집계 코드에서 text 타입은 집계가 불가능 keyword 타입만 가능 따라서 .keyword 필수로 붙여야 함
                            .size(11) // 상위 11개만 나중에 자기자신 빼기 때문에
                    )
            );

            // 검색 요청 생성
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("foreign_news")
                    .query(boolQuery)
                    .size(0) // doc 자체는 필요 없으므로 size 0
                    .aggregations("related_keywords", agg)
            );

            // 검색 실행
            var response = esClient.search(searchRequest, ForeignNewsElastic.class);

            // aggression 결과 파싱
            StringTermsAggregate aggregation = response.aggregations()
                    .get("related_keywords")
                    .sterms();

            // 연관 키워드 리스트 반환
            List<String> relatedKeywords = aggregation.buckets().array().stream()
                    .map(bucket -> bucket.key().stringValue())
                    .filter(rel -> !rel.equals(keyword)) // 자기 자신 제외
                    .collect(Collectors.toList());

            long end = System.currentTimeMillis();
            System.out.println("연관어  ====> 처리 시간: " + (end - start) + "ms");

            return RelatedKeywordsResponse.builder()
                    .keyword(keyword)
                    .relatedKeywords(relatedKeywords)
                    .build();

        } catch (IOException e) {
            throw new RuntimeException("연관어 검색 실패", e);
        }
    }

    public KeywordRankingData getKeywordRanking(String category, int period, boolean isKorea) {
        // 아직 국내 뉴스 부분 추가 안됨 추후 수정 예정
        try {
            // 유효성 검사
//            Validation.validateCategoryAndPeriod(category, period);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime from = now.minusDays(period);

            String gte = from.format(formatter);
            String lte = now.format(formatter);
            String requestId = UUID.randomUUID().toString();

            Query boolQuery = Query.of(q -> q.bool(b -> {
                List<Query> mustQueries = new ArrayList<>();

                if (!category.equalsIgnoreCase("all")) {
                    mustQueries.add(Query.of(m -> m.term(t -> t
                            .field("categories")
                            .value(FieldValue.of(category))
                    )));
                }
                mustQueries.add(Query.of(m -> m.range(r -> r
                        .date(d -> d
                                .field("published_at")
                                .gte(gte)
                                .lte(lte)
                        )
                )));
                return b.must(mustQueries);
            }));


            // Slice Scroll 로 전체 뉴스 조회
            List<String> idList = sliceScroll(boolQuery);

            Map<String, Object> payload = new HashMap<>();
            payload.put("newsIds", idList);
            payload.put("requestId", requestId);
            payload.put("callbackUrl", callBackUrl + "/api/search/keyword_ranking_callback");

            CompletableFuture<KeywordRankingData> future = new CompletableFuture<>();
            pendingCompareResults.put(requestId, future);

            // kafka로 전송
            String json = objectMapper.writeValueAsString(payload);
            kafkaTemplate.send("keyword_ranking", json);

            // 더미 반환값
            KeywordRankingData data = future.get(timeout, TimeUnit.SECONDS);

            return data;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new NoSuchElementException("키워드 랭킹을 불러오지 못했습니다.");
        }

    }

    public CompletableFuture<?> removeFuture(String requestId) {
        return pendingCompareResults.remove(requestId);
    }

    public SentimentMentionData getWorldwide(String keyword, String keywordMind, String category, int period) {
        try {
            // 유효성 검사
//            Validation.validateCategoryAndPeriod(category, period);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime from = now.minusDays(period);

            String gte = from.format(formatter);
            String lte = now.format(formatter);
            String requestId = UUID.randomUUID().toString();

            // 필터링 Query
            Query boolQuery = Query.of(q -> q.bool(b -> {
                List<Query> mustQueries = new ArrayList<>();

                mustQueries.add(Query.of(m -> m.term(t -> t
                        .field("keywords")
                        .value(FieldValue.of(keyword))
                )));

                if (!keywordMind.isEmpty()) {
                    mustQueries.add(Query.of(m -> m.term(t -> t
                            .field("keywords")
                            .value(FieldValue.of(keywordMind))
                    )));
                }

                if (!category.equalsIgnoreCase("all")) {
                    mustQueries.add(Query.of(m -> m.term(t -> t
                            .field("categories")
                            .value(FieldValue.of(category))
                    )));
                }

                mustQueries.add(Query.of(m -> m.range(r -> r
                        .date(d -> d
                                .field("published_at")
                                .gte(gte)
                                .lte(lte)
                        )
                )));
                return b.must(mustQueries);
            }));


            List<String> idList = sliceScroll(boolQuery);

            // idList와 keyword를 함께 담을 수 있는 Map을 만듦
            Map<String, Object> payload = new HashMap<>();
            payload.put("keyword", keyword);
            payload.put("keyword_mind", keywordMind);
            payload.put("ids", idList);
            payload.put("requestId", requestId);
            payload.put("callbackUrl", callBackUrl + "/api/search/worldwide_callback");

            CompletableFuture<SentimentMentionData> future = new CompletableFuture<>();
            pendingCompareResults.put(requestId, future);

            // Map을 JSON 문자열로 변환 & kafka로 전송
            String json = objectMapper.writeValueAsString(payload);
            kafkaTemplate.send("worldwide", json);

            SentimentMentionData data = future.get(timeout, TimeUnit.SECONDS);

            return data;
        } catch (Exception e) {
            throw new NoSuchElementException("언급량, 감정 수치를 불러올 수 없습니다.");
        }
    }


    public List<String> sliceScroll(Query query) {
        long start = System.currentTimeMillis();

        int pageSize = 10000; // 한 페이지에 처리할 개수 일단 1,000, 10,000 거의 비슷함
        int sliceCount = 4; // 병렬 처리 개수 cpu 성능 따라 다른데 일단 4개 이상 넘어가면 차이 없는거 같음
        ExecutorService executor = Executors.newFixedThreadPool(sliceCount); // 4개의 스레드 풀 생성
        List<String> idList = Collections.synchronizedList(new ArrayList<>()); // 멀티스레드에서 접근 가능한 리스트 생성
        CountDownLatch latch = new CountDownLatch(sliceCount); // 모든 스레드가 끝날 때까지 메인 스레드 대기, 한번에 보낼려고

        // 스레드 별 slice 영역 할당
        for (int sliceId = 0; sliceId < sliceCount; sliceId++) {
            final int currentSlice = sliceId;

            executor.submit(() -> {
                try {
                    String scrollId = null;
                    var response = esClient.search(s -> s
                                    .index("foreign_news")
                                    .scroll(t -> t.time("2m"))
                                    .size(10000)
                                    .query(query)
                                    .slice(sl -> sl
                                            .field("_id") // id를 기준으로 데이터를 나누고 sliceId에 해당하는 데이터만 가져옴
                                            .id(String.valueOf(currentSlice))
                                            .max(sliceCount)
                                    )
                                    .source(src -> src.filter(f -> f.includes("id")))
                            , ForeignNewsElastic.class);

                    response.hits().hits().forEach(hit -> idList.add(hit.source().getId()));
                    scrollId = response.scrollId();

                    // 데이터 없을 때까지 스크롤 반복
                    while (true) {
                        String finalScrollId = scrollId;
                        var scrollResponse = esClient.scroll(sc -> sc
                                        .scroll(t -> t.time("2m"))
                                        .scrollId(finalScrollId)
                                , ForeignNewsElastic.class);

                        if (scrollResponse.hits().hits().isEmpty()) break;

                        scrollResponse.hits().hits().forEach(hit -> idList.add(hit.source().getId()));
                        scrollId = scrollResponse.scrollId();
                    }

                    // scroll 종료
                    String finalScrollId1 = scrollId;
                    esClient.clearScroll(c -> c.scrollId(finalScrollId1));
                } catch (Exception e) {
                    log.error("Slice scroll 실패 - slice {}", currentSlice, e);
                } finally {
                    latch.countDown(); // 스레드 종료 알림
                }
            });
        }

        // 모든 스레드 종료 대기
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executor.shutdown();

        long end = System.currentTimeMillis();
        int size = idList.size();
        System.out.println("뉴스 " + size + "개 ====> 조회 시간: " + (end - start) + "ms");
        return idList;
    }

}
