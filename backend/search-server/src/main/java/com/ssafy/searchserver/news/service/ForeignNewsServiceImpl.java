package com.ssafy.searchserver.news.service;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.json.JsonData;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import com.ssafy.searchserver.news.dto.ForeignNewsListResponse;
import com.ssafy.searchserver.news.dto.ForeignNewsResponse;
import com.ssafy.searchserver.news.entity.ForeignNewsElastic;
import com.ssafy.searchserver.news.entity.ForeignNewsMongo;
import com.ssafy.searchserver.news.repository.ForeignNewsElasticsearchRepository;
import com.ssafy.searchserver.news.repository.ForeignNewsMongoDBRepository;

@Service
@RequiredArgsConstructor
public class ForeignNewsServiceImpl implements ForeignNewsService {

	private final ForeignNewsMongoDBRepository mongoDBRepository;
	private final ForeignNewsElasticsearchRepository esRepository;
	private final ElasticsearchClient esClient;


	@Override
	public ForeignNewsMongo save(ForeignNewsMongo news) {

		return mongoDBRepository.save(news);
	}

	@Override
	public ForeignNewsListResponse getNewsList() {
		List<ForeignNewsResponse> newsList = mongoDBRepository.findAll().stream()
			.map(news -> ForeignNewsResponse.builder()
				.id(news.getId())
				.title(news.getTitle())
				.description(news.getDescription())
				.publishedAt(news.getPublishedAt())
				.imageUrl(news.getImageUrl())
				.url(news.getUrl())
				.build())
			.collect(Collectors.toList());

		return ForeignNewsListResponse.builder()
			.code("SUCCESS")
			.success(true)
			.message("요청 성공")
			.data(newsList)
			.build();
	}



	@Override
	public ForeignNewsResponse save(ForeignNewsElastic news) {
		try {
			esClient.index(i -> i
					.index("foreign_news")
					.id(news.getId())
					.document(news)
					.refresh(co.elastic.clients.elasticsearch._types.Refresh.True)
			);
		} catch (IOException e) {
			throw new RuntimeException("Elasticsearch 저장 실패", e);
		}

		return ForeignNewsResponse.builder()
				.id(news.getId())
				.title(news.getTitle())
				.description(news.getDescription())
				.url(news.getUrl())
				.imageUrl(news.getImageUrl())
				.publishedAt(news.getPublishedAt())
				.categories(news.getCategories())
				.country(news.getCountry())
				.keywords(news.getKeywords())
				.sentiment(news.getSentiment())
				.build();
	}
	@Override
	public ForeignNewsListResponse searchByKeyword(String keyword) {
		List<ForeignNewsResponse> newsList = esRepository.findByKeywords(keyword).stream()
			.map(news -> ForeignNewsResponse.builder()
				.id(news.getId())
				.title(news.getTitle())
				.description(news.getDescription())
				.publishedAt(news.getPublishedAt())
				.imageUrl(news.getImageUrl())
				.url(news.getUrl())
				.country(news.getCountry())
				.categories(news.getCategories())
				.keywords(news.getKeywords())
				.build())
			.collect(Collectors.toList());

		return ForeignNewsListResponse.builder()
			.code("SUCCESS")
			.success(true)
			.message("요청 성공")
			.data(newsList)
			.build();
	}
	@Override
	public ForeignNewsListResponse searchByCategory(String category) {
		List<ForeignNewsResponse> newsList = esRepository.findByCategories(
				category).stream()
			.map(news -> ForeignNewsResponse.builder()
				.id(news.getId())
				.title(news.getTitle())
				.description(news.getDescription())
				.publishedAt(news.getPublishedAt())
				.imageUrl(news.getImageUrl())
				.url(news.getUrl())
				.country(news.getCountry())
				.categories(news.getCategories())
				.keywords(news.getKeywords())
				.build())
			.collect(Collectors.toList());

		return ForeignNewsListResponse.builder()
			.code("SUCCESS")
			.success(true)
			.message("요청 성공")
			.data(newsList)
			.build();
	}




	@Override
	public ForeignNewsListResponse searchByPeriod(int period) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime from = now.minus(period, ChronoUnit.DAYS);

		// ES 쿼리 호출
		List<ForeignNewsElastic> searchResult = esRepository.findByPublishedAtBetween(from, now);

		List<ForeignNewsResponse> newsList = searchResult.stream()
			.map(news -> ForeignNewsResponse.builder()
				.id(news.getId())
				.title(news.getTitle())
				.description(news.getDescription())
				.publishedAt(news.getPublishedAt())
				.imageUrl(news.getImageUrl())
				.url(news.getUrl())
				.country(news.getCountry())
				.categories(news.getCategories())
				.keywords(news.getKeywords())
				.build())
			.collect(Collectors.toList());

		return ForeignNewsListResponse.builder()
			.code("SUCCESS")
			.success(true)
			.message("요청 성공")
			.data(newsList)
			.build();
	}


	@Override
	public ForeignNewsListResponse searchByKeywordCategoryAndPeriod(String keyword, String category, int period) {
		try {
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime from = now.minusDays(period);
			String fromDateStr = from.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

			Query boolQuery = Query.of(q -> q.bool(b -> b
					.must(List.of(
							Query.of(m -> m.terms(t -> t
									.field("keywords")
									.terms(ts -> ts.value(List.of(FieldValue.of(keyword))))
							)),
							Query.of(m -> m.terms(t -> t
									.field("categories")
									.terms(ts -> ts.value(List.of(FieldValue.of(category))))
							))
//							Query.of(m -> m.range(r -> r
//									.field("published_at")
//									.gte(fromDateStr)
//							))
					))
			));

			// SearchRequest 구성
			var searchRequest = SearchRequest.of(s -> s
					.index("foreign_news")
					.query(boolQuery)
					.size(30)
					.sort(so -> so.field(f -> f
							.field("published_at")
							.order(SortOrder.Desc)
					))
			);

			var response = esClient.search(searchRequest, ForeignNewsElastic.class);

			List<ForeignNewsResponse> newsList = response.hits().hits().stream()
					.map(hit -> {
						ForeignNewsElastic news = hit.source();
						return ForeignNewsResponse.builder()
								.id(news.getId())
								.title(news.getTitle())
								.description(news.getDescription())
								.url(news.getUrl())
								.imageUrl(news.getImageUrl())
								.publishedAt(news.getPublishedAt())
								.categories(news.getCategories())
								.country(news.getCountry())
								.keywords(news.getKeywords())
								.sentiment(news.getSentiment())
								.build();
					})
					.collect(Collectors.toList());

			return ForeignNewsListResponse.builder()
					.code("SUCCESS")
					.success(true)
					.message("요청 성공")
					.data(newsList)
					.build();

		} catch (IOException e) {
			return ForeignNewsListResponse.builder()
					.code("FAIL")
					.success(false)
					.message("Elasticsearch 검색 실패: " + e.getMessage())
					.data(List.of())
					.build();
		}



	}
}
