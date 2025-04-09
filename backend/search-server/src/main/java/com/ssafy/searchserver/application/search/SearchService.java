package com.ssafy.searchserver.application.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.searchserver.interfaces.search.dto.KeywordRankingData;
import com.ssafy.searchserver.interfaces.search.dto.KeywordResponse;
import com.ssafy.searchserver.interfaces.search.dto.RelatedKeywordsResponse;
import com.ssafy.searchserver.domain.search.model.ForeignNewsElastic;
import com.ssafy.searchserver.interfaces.search.dto.SentimentMentionData;

import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
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
	private final RedisTemplate<String, Object> redisTemplate;
	private final Map<String, CompletableFuture<?>> pendingCompareResults = new ConcurrentHashMap<>();
	@Value("${call_back_url}")
	private String callBackUrl;
	private final int timeout = 60;

	public String selectNews(boolean isKorea) {
		return isKorea ? "domestic_news" : "foreign_news";
	}


	public RelatedKeywordsResponse getRelatedKeywords(String keyword, String category, int period, boolean isKorea) {
		try {
			// 프론트 첫 메인 화면 진입 시 키워드 1위 반영
			// 람다에서는 final 만 들어갈 수 있어서 따로 뺌
			String index = selectNews(isKorea);
			System.out.println("index: " + index);


			String tempKeyword = keyword;
			if (tempKeyword.isEmpty()) {
				String firstKeyword = getFirstKeyword(period, category, isKorea);
				tempKeyword = firstKeyword;
			}

			String searchKeyword = tempKeyword;
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
					.value(FieldValue.of(searchKeyword))
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
				.index(index)
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
				.filter(rel -> !rel.equals(searchKeyword)) // 자기 자신 제외
				.collect(Collectors.toList());

			long end = System.currentTimeMillis();
			System.out.println("연관어  ====> 처리 시간: " + (end - start) + "ms");

			return RelatedKeywordsResponse.builder()
				.keyword(searchKeyword)
				.relatedKeywords(relatedKeywords)
				.build();

		} catch (IOException e) {
			throw new RuntimeException("연관어 검색 실패", e);
		}
	}

	public KeywordRankingData getKeywordRanking(String category, int period, boolean isKorea) {
		try {
			// 조건에 맞는 Redis 키 생성 (ex: "keyword_ranking:politics:7:false")
			String redisKey = String.format("keyword_ranking:%s:%d:%b", category, period, isKorea);
			// Redis에서 해당 키의 값을 Object로 불러오기
			Object rawData = redisTemplate.opsForValue().get(redisKey);
			if (rawData == null) {
				throw new NoSuchElementException("키워드 랭킹 데이터가 존재하지 않습니다.");
			}
			// rawData(LinkedHashMap 등)를 KeywordRankingData 객체로 변환
			KeywordRankingData data = objectMapper.convertValue(rawData, KeywordRankingData.class);
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

			String tempKeyword = keyword;
			if (tempKeyword.isEmpty()) {
				boolean isKorea = false;
				String firstKeyword = getFirstKeyword(period, category, isKorea);
				tempKeyword = firstKeyword;
			}

			String searchKeyword = tempKeyword;

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
					.value(FieldValue.of(searchKeyword))
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

			List<String> idList = sliceScrollSendAll(boolQuery);

			// idList와 keyword를 함께 담을 수 있는 Map을 만듦
			Map<String, Object> payload = new HashMap<>();
			payload.put("keyword", searchKeyword);
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

	public List<String> sliceScrollSendAll(Query query) {
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

	public void  triggerKeywordRanking(String category, int period, boolean isKorea) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
			LocalDateTime to = LocalDateTime.now();
			LocalDateTime from = to.minusDays(period);
			String gte = from.format(formatter);
			String lte = to.format(formatter);

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

			// isKorea 값에 따라 사용할 인덱스 결정
			String index = isKorea ? "domestic_news" : "foreign_news";
			// ES에서 뉴스 ID 목록 조회 (slice scroll 방식)
			String requestId = UUID.randomUUID().toString();
			sliceScrollSendPartition(boolQuery, requestId, "/api/search/keyword_ranking_callback", index, category,
				isKorea,
				period);

		} catch (Exception e) {
			log.error("키워드 랭킹 통계 요청 전송 실패", e);
			throw new RuntimeException("키워드 랭킹 통계 요청 전송 실패", e);
		}
	}

	public void triggerHourlyKeywordRanking(String category, boolean isKorea) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
			LocalDateTime to = LocalDateTime.now();
			LocalDateTime from = to.minusHours(1); //
			int currentHour = to.getHour() + 100;

			String gte = from.format(formatter);
			String lte = to.format(formatter);

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

			String index = isKorea ? "domestic_news" : "foreign_news";
			String requestId = UUID.randomUUID().toString();

			sliceScrollSendPartition(
				boolQuery,
				requestId,
				"/api/search/keyword_ranking_callback",
				index,
				category,
				isKorea,
				currentHour
			);

		} catch (Exception e) {
			log.error("실시간 키워드 랭킹 요청 실패", e);
			throw new RuntimeException("실시간 키워드 랭킹 요청 실패", e);
		}
	}


	public void sliceScrollSendPartition(Query query, String requestId, String callBackPath, String index,
		String category,
		boolean isKorea, int period) {
		long start = System.currentTimeMillis();

		int pageSize = 10000; // 한 페이지에 처리할 개수 일단 1,000, 10,000 거의 비슷함
		int sliceCount = 4; // 병렬 처리 개수 cpu 성능 따라 다른데 일단 4개 이상 넘어가면 차이 없는거 같음
		ExecutorService executor = Executors.newFixedThreadPool(sliceCount); // 4개의 스레드 풀 생성
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
					// 10,000건 조회한 리스트를 모으는게 아니라 카프카로 바로 전송
					scrollId = response.scrollId();
					sendKafkaBatch(response.hits().hits(), requestId, callBackPath, false, category, isKorea,
						period); // 첫페이지 결고ㅓㅏ
					// 데이터 없을 때까지 스크롤 반복
					while (true) {
						String finalScrollId = scrollId;
						var scrollResponse = esClient.scroll(sc -> sc
								.scroll(t -> t.time("2m"))
								.scrollId(finalScrollId)
							, ForeignNewsElastic.class);

						if (scrollResponse.hits().hits().isEmpty())
							break;
						// 이 후 페이지 결과들
						scrollId = scrollResponse.scrollId();
						sendKafkaBatch(response.hits().hits(), requestId, callBackPath, false, category, isKorea,
							period);
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

		Map<String, Object> lastPayload = new HashMap<>();
		lastPayload.put("newsIds", new ArrayList<>()); // 실제 데이터는 없음
		lastPayload.put("requestId", requestId);
		lastPayload.put("callbackUrl", callBackUrl + callBackPath);
		lastPayload.put("isLastBatch", true); // 통계 서버에서 집계 시작 신호
		lastPayload.put("category", category);
		lastPayload.put("period", period);
		lastPayload.put("isKorea", isKorea);

		try {
			String json = objectMapper.writeValueAsString(lastPayload);
			kafkaTemplate.send("keyword_ranking", json);
		} catch (Exception e) {
			log.error("최종 배치 알림 전송 실패", e);
		}

		long end = System.currentTimeMillis();
		System.out.println("slice scroll 전송 완료 ====> 처리 시간: " + (end - start) + "ms");
	}

	// 배치가 끝났다는걸 알리기 위한 코드 데이터는 안들어감
	public void sendKafkaBatch(List<Hit<ForeignNewsElastic>> hits, String requestId, String callbackPath,
		boolean isLastBatch, String category, boolean isKorea, int period) {
		if (hits.isEmpty())
			return;

		List<String> ids = hits.stream()
			.map(hit -> hit.source().getId())
			.collect(Collectors.toList());

		Map<String, Object> payload = new HashMap<>();
		payload.put("newsIds", ids);
		payload.put("requestId", requestId);
		payload.put("callbackUrl", callBackUrl + callbackPath);
		payload.put("isLastBatch", isLastBatch); // 마지막 배치 여부를 명시
		payload.put("category", category);
		payload.put("period", period);
		payload.put("isKorea", isKorea);

		try {
			String json = objectMapper.writeValueAsString(payload);
			kafkaTemplate.send("keyword_ranking", json);
		} catch (Exception e) {
			log.error("Kafka 전송 실패 - requestId: {}", requestId, e);
		}
	}

	public String getFirstKeyword(int period, String category, boolean isKorea) {
		String redisKey = String.format("keyword_ranking:%s:%d:%b", category, period, isKorea);
		// Redis에서 해당 키의 값을 Object로 불러오기

		Object obj = redisTemplate.opsForValue().get(redisKey);
		String firstKeyword = "";
		ObjectMapper objectMapper = new ObjectMapper();
		KeywordRankingData keywordRankingData = objectMapper.convertValue(obj, KeywordRankingData.class);

		if (keywordRankingData.getKeywords() != null && !keywordRankingData.getKeywords().isEmpty()) {
			KeywordResponse first = keywordRankingData.getKeywords().get(0);
			firstKeyword = first.getName();
			System.out.println(firstKeyword);
		}
		return firstKeyword;
	}

}
