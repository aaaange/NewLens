package com.ssafy.searchserver.application.country;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.ssafy.searchserver.common.exeception.CustomException;
import com.ssafy.searchserver.common.exeception.ErrorCode;
import com.ssafy.searchserver.common.util.Validation;
import com.ssafy.searchserver.interfaces.country.dto.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.searchserver.domain.search.model.ForeignNewsElastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CountryService {

    private final ElasticsearchClient esClient;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Map<String, CompletableFuture<?>> pendingCompareResults = new ConcurrentHashMap<>();
    @Value("${call_back_url}")
    private String callBackUrl;
    private final int timeout = 60;

    public String selectNews(boolean isKorea) {
        return isKorea ? "domestic_news" : "foreign_news";
    }

    public DashboardData getDashboard(String category, int period, String keyword, String keywordMind, String country,
                                      boolean isKorea) {
        try {
            String index = selectNews(isKorea);
            System.out.println(index);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime from = now.minusDays(period);

            String gte = from.format(formatter);
            String lte = now.format(formatter);
            String requestId = UUID.randomUUID().toString();
            int size = keywordMind.isEmpty() ? 21 : 22;

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
                if (!country.isEmpty()) {
                    mustQueries.add(Query.of(m -> m.term(t -> t
                            .field("country.keyword")
                            .value(FieldValue.of(country))
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

            // 연관어 추출 Aggregation
            Aggregation agg = Aggregation.of(a -> a
                    .terms(t -> t
                            .field("keywords.keyword")
                            .size(size)
                    )
            );

            SearchRequest aggregationRequest = SearchRequest.of(s -> s
                    .index(index)
                    .query(boolQuery)
                    .size(0)
                    .aggregations("word_cloud", agg)
            );

            // ES에서 조회 두 개 다 데이터 구조가 같아서 클래스타입은 아무거나 써도 상관 없음
            var aggResponse = esClient.search(aggregationRequest, ForeignNewsElastic.class);

            // Aggregation 처리
            StringTermsAggregate aggregation = aggResponse.aggregations()
                    .get("word_cloud")
                    .sterms();

            List<KeywordResponse> wordCloud = aggregation.buckets().array().stream()
                    .filter(bucket -> {
                        String key = bucket.key().stringValue();
                        return !key.equals(keyword) && !key.equals(keywordMind);
                    })
                    .map(bucket -> KeywordResponse.builder()
                            .name(bucket.key().stringValue())
                            .count(bucket.docCount())
                            .build())
                    .toList();

            List<String> idList = sliceScroll(boolQuery, index);

            // kafka로 전달할 payload에 newsIds, page, size를 함께 포함
            Map<String, Object> payload = new HashMap<>();
            payload.put("newsIds", idList);
            payload.put("period", period);
            payload.put("keyword", keyword);
            payload.put("keyword_mind", keywordMind);
            payload.put("wordCloud", wordCloud);
            payload.put("requestId", requestId);
            payload.put("country", country);
            payload.put("isKorea", isKorea);
            payload.put("callbackUrl", callBackUrl + "/api/search/country/dashboard_callback");

            CompletableFuture<DashboardData> future = new CompletableFuture<>();
            pendingCompareResults.put(requestId, future);


            String json = objectMapper.writeValueAsString(payload);
            kafkaTemplate.send("dashboard", json);

            // 15초 대기
            DashboardData data = future.get(timeout, TimeUnit.SECONDS);

            return data;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public AnalysisData getGptDescription(String category, int period, String keyword, String keywordMind, String country, boolean isKorea) {
        try {
            String requestId = UUID.randomUUID().toString();
            CompletableFuture<String> future = new CompletableFuture<>();
            pendingCompareResults.put(requestId, future);


            // List<String> idList = getNewsByCountry(category, period, keyword, keywordMind, country);

            Map<String, Object> payload = new HashMap<>();
            payload.put("requestId", requestId);
            payload.put("keyword", keyword);
            // payload.put("newsIds", idList);
            payload.put("keyword_mind", keywordMind);
            payload.put("country", country);
            payload.put("isKorea", isKorea);
            payload.put("callbackUrl", callBackUrl + "/api/search/country/dashboard_gpt_callback");


            String json = objectMapper.writeValueAsString(payload);
            kafkaTemplate.send("dashboard_gpt", json);

            // 15초 대기
            String gptResult = future.get(timeout, TimeUnit.SECONDS);

            return AnalysisData.builder().analysis(gptResult).build();
        } catch (Exception e) {
            throw new RuntimeException("GPT 요약 요청 실패", e);
        }
    }

    public AnalysisData getCompareInfo(String category, int period, String keyword, String keywordMind,
                                       String country1, String country2) {
        try {
            // 유효성 검사
            List<String> country1NewsIds = getNewsByCountry(category, period, keyword, keywordMind, country1);
            //            Validation.validateCountryPeriodCategory(country1, period, category);
            List<String> country2NewsIds = getNewsByCountry(category, period, keyword, keywordMind, country2);
            //            Validation.validateCountryPeriodCategory(country2, period, category);
            String requestId = UUID.randomUUID().toString();

            CountryNewsMessage message = CountryNewsMessage.builder()
                    .keyword(keyword)
                    .keywordMind(keywordMind)
                    .country1(country1)
                    .country2(country2)
                    .country1NewsIds(country1NewsIds)
                    .country2NewsIds(country2NewsIds)
                    .requestId(requestId)
                    .callbackUrl(callBackUrl + "/api/search/country/compare_callback")
                    .build();

            // CompletableFuture 등록 (5초 대기)
            CompletableFuture<String> future = new CompletableFuture<>();
            pendingCompareResults.put(requestId, future);

            String json = objectMapper.writeValueAsString(message);
            kafkaTemplate.send("compare_info", json);

            // 15초 대기
            String gptResult = future.get(timeout, TimeUnit.SECONDS);

            return AnalysisData.builder().analysis(gptResult).build();

        } catch (Exception e) {
            throw new RuntimeException("GPT 요약 요청 실패", e);
        }
    }

    public CompletableFuture<?> removeFuture(String requestId) {
        return pendingCompareResults.remove(requestId);
    }

    public List<String> getNewsByCountry(String category, int period, String keyword, String keywordMind,
                                         String country) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime from = now.minusDays(period);

            String gte = from.format(formatter);
            String lte = now.format(formatter);

            Query boolQuery = Query.of(q -> q.bool(b -> {
                List<Query> mustQueries = new ArrayList<>();

                mustQueries.add(Query.of(m -> m.term(t -> t.field("keywords").value(FieldValue.of(keyword)))));
                if (!keywordMind.isEmpty()) {
                    mustQueries.add(Query.of(m -> m.term(t -> t.field("keywords").value(FieldValue.of(keywordMind)))));
                }

                mustQueries.add(Query.of(m -> m.term(t -> t.field("country.keyword").value(FieldValue.of(country)))));
                if (!category.equalsIgnoreCase("all")) {
                    mustQueries.add(Query.of(m -> m.term(t -> t.field("categories").value(FieldValue.of(category)))));
                }
                mustQueries.add(Query.of(m -> m.range(r -> r.date(d -> d
                        .field("published_at").gte(gte).lte(lte)
                ))));

                return b.must(mustQueries);
            }));

            var searchRequest = SearchRequest.of(s -> s
                    .index("foreign_news")
                    .query(boolQuery)
                    .size(3)
                    .source(src -> src.filter(f -> f.includes("id")))
            );

            var response = esClient.search(searchRequest, ForeignNewsElastic.class);

            return response.hits().hits().stream()
                    .map(hit -> hit.source().getId())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public NewsModalResponse getNewsNodal(String category, int period, String keyword, String keywordMind,
                                          String keywordCloud, String country, int page, int size,
                                          boolean isKorea) {
        try {
            // 유효성 검사
            //            Validation.validateCountryPeriodCategory(country, period, category);
            String index = selectNews(isKorea);
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

                if (!keywordCloud.isEmpty()) {
                    mustQueries.add(Query.of(m -> m.term(t -> t
                            .field("keywords")
                            .value(FieldValue.of(keywordCloud))
                    )));
                }

                if (!category.equalsIgnoreCase("all")) {
                    mustQueries.add(Query.of(m -> m.term(t -> t
                            .field("categories")
                            .value(FieldValue.of(category))
                    )));
                }
                if (!country.isEmpty()) {
                    mustQueries.add(Query.of(m -> m.term(t -> t
                            .field("country.keyword")
                            .value(FieldValue.of(country))
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

            List<String> idList = sliceScroll(boolQuery, index);

            // kafka로 전달할 payload에 newsIds, page, size를 함께 포함
            Map<String, Object> payload = new HashMap<>();
            payload.put("newsIds", idList);
            payload.put("page", page);
            payload.put("size", size);
            payload.put("requestId", requestId);
            payload.put("isKorea", isKorea);
            payload.put("callbackUrl", callBackUrl + "/api/search/country/news_modal_callback");

            CompletableFuture<NewsModalResponse> future = new CompletableFuture<>();
            pendingCompareResults.put(requestId, future);

            // Map을 JSON 문자열로 변환 & kafka로 전송
            String json = objectMapper.writeValueAsString(payload);
            kafkaTemplate.send("news_modal", json);

            // 15초 대기
            NewsModalResponse data = future.get(timeout, TimeUnit.SECONDS);

            return data;

        } catch (Exception e) {
            throw new RuntimeException("뉴스 모달창 데이터 검색 실패", e);
        }
    }

    public List<String> sliceScroll(Query query, String index) {
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
                                    .index(index)
                                    .scroll(t -> t.time("2m"))
                                    .size(pageSize)
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

                        if (scrollResponse.hits().hits().isEmpty())
                            break;

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
