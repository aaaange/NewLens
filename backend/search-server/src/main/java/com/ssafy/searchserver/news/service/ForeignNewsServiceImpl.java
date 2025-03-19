package com.ssafy.searchserver.news.service;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import java.time.Instant;
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
		esRepository.save(news);
		ForeignNewsResponse response = ForeignNewsResponse.builder()
			.id(news.getId())
			.title(news.getTitle())
			.description(news.getDescription())
			.url(news.getUrl())
			.imageUrl(news.getImageUrl()) // document의 imageUrl getter 호출
			.publishedAt(news.getPublishedAt()) // document getter 호출
			.categories(news.getCategories())
			.country(news.getCountry())
			.keywords(news.getKeywords())
			.sentiment(news.getSentiment())
			.build();
		return response;
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
		Instant now = Instant.now();
		Instant from = now.minus(period, ChronoUnit.DAYS);

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


}
