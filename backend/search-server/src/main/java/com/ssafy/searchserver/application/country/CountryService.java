package com.ssafy.searchserver.application.country;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


import com.ssafy.searchserver.interfaces.country.dto.*;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.searchserver.domain.search.model.ForeignNewsElastic;
import com.ssafy.searchserver.domain.search.repository.ForeignNewsMongoDBRepository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CountryService {

	private final ForeignNewsMongoDBRepository mongoDBRepository;
	private final ElasticsearchClient esClient;
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;
	private final Map<String, CompletableFuture<String>> pendingCompareResults = new ConcurrentHashMap<>();

	public DashboardData getDashboard(String category, int period, String keyword, String keywordMind,
		String keywordCloud, String country, boolean isKorea) {
		try {
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime from = now.minusDays(period);

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

				mustQueries.add(Query.of(m -> m.term(t -> t
					.field("country")
					.value(FieldValue.of(country))
				)));

				mustQueries.add(Query.of(m -> m.term(t -> t
					.field("categories")
					.value(FieldValue.of(category))
				)));

				mustQueries.add(Query.of(m -> m.range(r -> r
					.date(d -> d
						.field("published_at")
						.gte(from.toString())
						.lte(now.toString())
					)
				)));
				return b.must(mustQueries);
			}));

			// 연관어 추출 Aggregation
			Aggregation agg = Aggregation.of(a -> a
				.terms(t -> t
					.field("keywords")
					.size(20)
				)
			);

			// ID 조회용 searchRequest
			var searchRequest = SearchRequest.of(s -> s
					.index("foreign_news")
					.query(boolQuery)
					.size(10000)
					.aggregations("word_cloud", agg)
					.source(src -> src.filter(f -> f.includes("id")))
				// 우리는 id만 필요하니까 id만 반환
			);

			// ES에서 조회
			var response = esClient.search(searchRequest, ForeignNewsElastic.class);

			// ES에서 필터링 거친 뉴스 id 리스트 리턴
			List<String> idList = response.hits().hits().stream()
				.map(hit -> hit.source().getId())
				.collect(Collectors.toList());

			// kafka로 전달할 payload에 newsIds, page, size를 함께 포함
			Map<String, Object> payload = new HashMap<>();
			payload.put("newsIds", idList);
			payload.put("period", period);

			// Map을 JSON 문자열로 변환 & kafka로 전송
			String json = objectMapper.writeValueAsString(payload);
			kafkaTemplate.send("dashboard", json);

			// 4. Aggregation 처리
			StringTermsAggregate aggregation = response.aggregations()
				.get("word_cloud")
				.sterms();

			List<KeywordResponse> wordCloud = aggregation.buckets().array().stream()
				.filter(rel -> !rel.equals(keyword)) // keyword1과 중복 제거
				.filter(rel -> !rel.equals(keywordMind))
				.filter(rel -> !rel.equals(keywordCloud))
				.map(bucket -> KeywordResponse.builder()
					.name(bucket.key().stringValue())
					.count(bucket.docCount())
					.build())
				.collect(Collectors.toList());

			System.out.println("워드 클라우드");
			for (KeywordResponse keywordResponse : wordCloud) {
				System.out.println(keywordResponse.toString());
			}

			return DashboardData.builder().keywords(wordCloud).build();
		} catch (Exception e) {
			throw new RuntimeException("Dashboard 데이터 검색 실패", e);
		}
	}

	public CompareInfoResponse getCompareInfo(String category, int period, String keyword, String keywordMind,
		String country1, String country2) {
		try {
			List<String> country1NewsIds = getNewsByCountry(category, period, keyword, keywordMind, country1);
			List<String> country2NewsIds = getNewsByCountry(category, period, keyword, keywordMind, country2);
			String requestId = UUID.randomUUID().toString();

			CountryNewsMessage message = CountryNewsMessage.builder()
				.keyword(keyword)
				.keywordMind(keywordMind)
				.country1(country1)
				.country2(country2)
				.country1NewsIds(country1NewsIds)
				.country2NewsIds(country2NewsIds)
				.requestId(requestId)
				.callbackUrl("http://localhost:8080/api/search/country/compare-callback")
				.build();

			// CompletableFuture 등록 (5초 대기)
			CompletableFuture<String> future = new CompletableFuture<>();
			pendingCompareResults.put(requestId, future);

			String json = objectMapper.writeValueAsString(message);
			kafkaTemplate.send("compare-info", json);

			// 5초 대기
			String gptResult = future.get(5, TimeUnit.SECONDS);

			AnalysisData data = AnalysisData.builder().analysis(gptResult).build();

			return CompareInfoResponse.builder()
				.code("SUCCESS")
				.success(true)
				.message("요약 성공")
				.data(data)
				.build();

		} catch (Exception e) {
			throw new RuntimeException("GPT 요약 요청 실패", e);
		}
	}

	public CompletableFuture<String> removeFuture(String requestId) {
		return pendingCompareResults.remove(requestId);
	}

	public List<String> getNewsByCountry(String category, int period, String keyword, String keywordMind,
		String country) {
		try {
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime from = now.minusDays(period);

			Query boolQuery = Query.of(q -> q.bool(b -> {
				List<Query> mustQueries = new ArrayList<>();

				mustQueries.add(Query.of(m -> m.term(t -> t.field("keywords").value(FieldValue.of(keyword)))));
				if (!keywordMind.isEmpty()) {
					mustQueries.add(Query.of(m -> m.term(t -> t.field("keywords").value(FieldValue.of(keywordMind)))));
				}

				mustQueries.add(Query.of(m -> m.term(t -> t.field("country").value(FieldValue.of(country)))));
				mustQueries.add(Query.of(m -> m.term(t -> t.field("categories").value(FieldValue.of(category)))));
				mustQueries.add(Query.of(m -> m.range(r -> r.date(d -> d
					.field("published_at").gte(from.toString()).lte(now.toString())
				))));

				return b.must(mustQueries);
			}));

			var searchRequest = SearchRequest.of(s -> s
				.index("foreign_news")
				.query(boolQuery)
				.size(5)
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
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime from = now.minusDays(period);

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

				mustQueries.add(Query.of(m -> m.term(t -> t
					.field("categories")
					.value(FieldValue.of(category))
				)));

				mustQueries.add(Query.of(m -> m.term(t -> t
					.field("country")
					.value(FieldValue.of(country))
				)));

				mustQueries.add(Query.of(m -> m.range(r -> r
					.date(d -> d
						.field("published_at")
						.gte(from.toString())
						.lte(now.toString())
					)
				)));
				return b.must(mustQueries);
			}));

			// ID 조회용 searchRequest
			var searchRequest = SearchRequest.of(s -> s
					.index("foreign_news")
					.query(boolQuery)
					.size(10000)
					.source(src -> src.filter(f -> f.includes("id")))
				// 우리는 id만 필요하니까 id만 반환
			);

			// ES에서 조회
			var response = esClient.search(searchRequest, ForeignNewsElastic.class);

			// ES에서 필터링 거친 뉴스 id 리스트 리턴
			List<String> idList = response.hits().hits().stream()
				.map(hit -> hit.source().getId())
				.collect(Collectors.toList());

            // kafka로 전달할 payload에 newsIds, page, size를 함께 포함
            Map<String, Object> payload = new HashMap<>();
            payload.put("newsIds", idList);
            payload.put("page", page);
            payload.put("size", size);

            // Map을 JSON 문자열로 변환 & kafka로 전송
            String json = objectMapper.writeValueAsString(payload);
            kafkaTemplate.send("news-modal", json);

            return NewsModalResponse.builder().build();

		} catch (Exception e) {
			throw new RuntimeException("뉴스 모달창 데이터 검색 실패", e);
		}
	}

}
