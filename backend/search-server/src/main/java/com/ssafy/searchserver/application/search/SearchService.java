package com.ssafy.searchserver.application.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.searchserver.interfaces.search.dto.ForeignNewsListResponse;
import com.ssafy.searchserver.interfaces.search.dto.ForeignNewsResponse;
import com.ssafy.searchserver.interfaces.search.dto.RelatedKeywordsResponse;
import com.ssafy.searchserver.domain.search.model.ForeignNewsElastic;
import com.ssafy.searchserver.domain.search.model.ForeignNewsMongo;
import com.ssafy.searchserver.domain.search.repository.ForeignNewsMongoDBRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

	private final ForeignNewsMongoDBRepository mongoDBRepository;
	private final ElasticsearchClient esClient;
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;

	public ForeignNewsMongo save(ForeignNewsMongo news) {
		return mongoDBRepository.save(news);
	}

	public ForeignNewsListResponse getNewsList() {
		List<ForeignNewsResponse> newsList = mongoDBRepository.findAll().stream()
			.map(news -> ForeignNewsResponse.builder()
				.id(news.getId())
				.title(news.getTitle())
				.description(news.getDescription())
				.imageUrl(news.getImage_url())
				.url(news.getUrl())
				.categories(news.getCategories())
				.country(news.getCountry())
				.keywords(news.getKeywords())
				.sentiment(news.getSentiment())
				.publishedAt(news.getPublished_at())
				.rawDataRef(news.getRawDataRef())
				.build())
			.collect(Collectors.toList());

		return ForeignNewsListResponse.builder()
			.code("SUCCESS")
			.success(true)
			.message("요청 성공")
			.data(newsList)
			.build();
	}

	public ForeignNewsResponse save(ForeignNewsElastic news) {
		try {
			esClient.index(i -> i
					.index("foreign_news") // 인덱스명 지정 MySQL의 테이블 지정느낌
					.id(news.getId()) // document의 id 지정
					.document(news) // 저장할 document 객체
					.refresh(co.elastic.clients.elasticsearch._types.Refresh.True) // 저장 후 바로 검색 가능
				// 대량 저장 시에는 성능 떨어짐 추후 최적화 예정
			);
		} catch (IOException e) {
			throw new RuntimeException("Elasticsearch 저장 실패", e);
		}

		return ForeignNewsResponse.builder()
			.id(news.getId())
			.publishedAt(news.getPublishedAt())
			.categories(news.getCategories())
			.country(news.getCountry())
			.keywords(news.getKeywords())
			.sentiment(news.getSentiment())
			.build();
	}

	public RelatedKeywordsResponse getRelatedKeywords(String keyword, String category, int period) {
		try {
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime from = now.minusDays(period);

			// 필터링 Query
			Query boolQuery = Query.of(q -> q.bool(b -> b
				.must(List.of(
					// keywords 배열 안에 입력된 keyword가 존재하지
					Query.of(m -> m.terms(t -> t
						.field("keywords")
						.terms(ts -> ts.value(List.of(FieldValue.of(keyword))))
					)),

					Query.of(m -> m.terms(t -> t
						.field("categories")
						.terms(ts -> ts.value(List.of(FieldValue.of(category))))
					)),

					Query.of(m -> m.range(r -> r
						.date(d -> d
							.field("published_at")
							.gte(from.toString())
							.lte(now.toString())
						)
					))
				))
			));

			//  연관 키워드 추출을 위한 terms aggregation
			// keyword가 포함된 뉴스에서 다른 키워드들을 연관어로 뽑음
			Aggregation agg = Aggregation.of(a -> a
				.terms(t -> t
					.field("keywords")
					.size(10) // 상위 10개만
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
			var response = esClient.search(searchRequest, ForeignNewsMongo.class);

			// aggression 결과 파싱
			StringTermsAggregate aggregation = response.aggregations()
				.get("related_keywords")
				.sterms();

			// 연관 키워드 리스트 반환
			List<String> relatedKeywords = aggregation.buckets().array().stream()
				.map(bucket -> bucket.key().stringValue())
				.filter(rel -> !rel.equals(keyword)) // 자기 자신 제외
				.collect(Collectors.toList());

			return RelatedKeywordsResponse.builder()
				.keyword(keyword)
				.relatedKeywords(relatedKeywords)
				.build();

		} catch (IOException e) {
			throw new RuntimeException("연관어 검색 실패", e);
		}
	}

	public ForeignNewsListResponse getKeywordRanking(String category, int period, boolean is_korea) {
		// 아직 국내 뉴스 부분 추가 안됨 추후 수정 예정
		try {
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime from = now.minusDays(period);

			Query boolQuery = Query.of(q -> q.bool(b -> b
				.must(List.of(
					// 카테고리 배열 안에 category가 존재하는 뉴스
					Query.of(m -> m.terms(t -> t
						.field("categories")
						.terms(ts -> ts.value(List.of(FieldValue.of(category))))
					)),
					// 뉴스 생성 시간이 기간 내에 포함되는 뉴스
					Query.of(m -> m.range(r -> r
						.date(d -> d
							.field("published_at")
							.gte(from.toString())
							.lte(now.toString())
						)
					))

				))
			));

			var searchRequest = SearchRequest.of(s -> s
					.index("foreign_news")
					.query(boolQuery)
					.source(src -> src.filter(f -> f.includes("id")))
				// 우리는 id만 필요하니까 id만 반환
			);

			// ES에서 조회
			var response = esClient.search(searchRequest, ForeignNewsMongo.class);
			// ES에서 필터링 거친 뉴스 id 리스트 리턴
			List<String> idList = response.hits().hits().stream()
				.map(hit -> hit.source().getId())
				.collect(Collectors.toList());

			// kafka로 전송
			String json = objectMapper.writeValueAsString(idList);
			kafkaTemplate.send("keyword-ranking", json);
			// kafkaTemplate.send("foreign_news", idList);

			List<ForeignNewsResponse> newsList = mongoDBRepository.findByIdIn(idList).stream()
				.map(news -> ForeignNewsResponse.builder()
					.id(news.getId())
					.publishedAt(news.getPublished_at())
					.title(news.getTitle())
					.description(news.getDescription())
					.imageUrl(news.getImage_url())
					.url(news.getUrl())
					.categories(news.getCategories())
					.country(news.getCountry())
					.keywords(news.getKeywords())
					.sentiment(news.getSentiment())
					.rawDataRef(news.getRawDataRef())
					.build())
				.collect(Collectors.toList());

			return ForeignNewsListResponse.builder()
				.code("SUCCESS")
				.success(true)
				.message("검색 결과 MongoDB에서 반환")
				.data(newsList)
				.build();
		} catch (Exception e) {
			return ForeignNewsListResponse.builder()
				.code("FAIL")
				.success(false)
				.message("Elasticsearch 검색 실패: " + e.getMessage())
				.data(List.of())
				.build();
		}

	}

	public ForeignNewsListResponse getWorldwide(String keyword, String category, int period) {
		try {
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime from = now.minusDays(period);

			Query boolQuery = Query.of(q -> q.bool(b -> b
				.must(List.of(
					// 키워드 배열 안에 keyword가 존재하는 뉴스
					Query.of(m -> m.terms(t -> t
						.field("keywords")
						.terms(ts -> ts.value(List.of(FieldValue.of(keyword))))
					)),
					// 카테고리 배열 안에 category가 존재하는 뉴스
					Query.of(m -> m.terms(t -> t
						.field("categories")
						.terms(ts -> ts.value(List.of(FieldValue.of(category))))
					)),
					// 뉴스 생성 시간이 기간 내에 포함되는 뉴스
					Query.of(m -> m.range(r -> r
						.date(d -> d
							.field("published_at")
							.gte(from.toString())
							.lte(now.toString())
						)
					))

				))
			));

			var searchRequest = SearchRequest.of(s -> s
					.index("foreign_news")
					.query(boolQuery)
					.size(1000)
					.source(src -> src.filter(f -> f.includes("id")))
				// 우리는 id만 필요하니까 id만 반환
			);

			// ES에서 조회
			var response = esClient.search(searchRequest, ForeignNewsMongo.class);
			// ES에서 필터링 거친 뉴스 id 리스트 리턴
			List<String> idList = response.hits().hits().stream()
				.map(hit -> hit.source().getId())
				.collect(Collectors.toList());

			// 7) idList와 keyword를 함께 담을 수 있는 Map을 만듦
			Map<String, Object> payload = new HashMap<>();
			payload.put("keyword", keyword);
			payload.put("ids", idList);

			// 8) Map을 JSON 문자열로 변환
			String json = objectMapper.writeValueAsString(payload);

			// 9) "worldwide" 토픽에 전송
			kafkaTemplate.send("worldwide", json);

			// // kafka로 전송
			// String json = objectMapper.writeValueAsString(idList);
			// kafkaTemplate.send("worldwide", json);
			// // kafkaTemplate.send("foreign_news", idList);

			List<ForeignNewsResponse> newsList = mongoDBRepository.findByIdIn(idList).stream()
				.map(news -> ForeignNewsResponse.builder()
					.id(news.getId())
					.publishedAt(news.getPublished_at())
					.title(news.getTitle())
					.description(news.getDescription())
					.imageUrl(news.getImage_url())
					.url(news.getUrl())
					.categories(news.getCategories())
					.country(news.getCountry())
					.keywords(news.getKeywords())
					.sentiment(news.getSentiment())
					.rawDataRef(news.getRawDataRef())
					.build())
				.collect(Collectors.toList());

			return ForeignNewsListResponse.builder()
				.code("SUCCESS")
				.success(true)
				.message("검색 결과 MongoDB에서 반환")
				.data(newsList)
				.build();
		} catch (Exception e) {
			return ForeignNewsListResponse.builder()
				.code("FAIL")
				.success(false)
				.message("Elasticsearch 검색 실패: " + e.getMessage())
				.data(List.of())
				.build();
		}

	}

}
