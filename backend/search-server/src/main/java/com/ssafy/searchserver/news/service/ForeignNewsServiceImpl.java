package com.ssafy.searchserver.news.service;



import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

import com.ssafy.searchserver.news.dto.ForeignNewsListResponse;
import com.ssafy.searchserver.news.dto.ForeignNewsResponse;
import com.ssafy.searchserver.news.entity.ForeignNewsMongo;
import com.ssafy.searchserver.news.repository.ForeignNewsMongoDBRepository;

@Service
@RequiredArgsConstructor
public class ForeignNewsServiceImpl implements ForeignNewsService {

	private final ForeignNewsMongoDBRepository mongoDBRepository;
	// private final ForeignNewsElasticsearchRepository esRepository;

	public ForeignNewsMongo save(ForeignNewsMongo news) {
		// ForeignNewsMongo news = ForeignNewsMongo.builder()
		// 	.title("세계 경제 동향 보고서")
		// 	.description("세계 경제가 회복세를 보이고 있습니다.")
		// 	.url("https://example.com/world-economy")
		// 	.publishedAt(Instant.of(2024, 12, 1, 10, 30))
		// 	.imageUrl("https://example.com/image.jpg")
		// 	.categories(Arrays.asList("경제", "국제"))
		// 	.country("US")
		// 	.keywords(Arrays.asList("경제", "회복", "세계"))
		// 	.sentiment(80)
		// 	.rawDataRef("ref_12345")
		// 	.build();

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

	// public ForeignNewsElastic save(ForeignNewsElastic news) {
	// 	return esRepository.save(news);
	// }
	//
	// public ForeignNewsListResponse searchByKeyword(String keyword) {
	// 	List<ForeignNewsResponse> newsList = esRepository.findByKeywordsContaining(keyword).stream()
	// 		.map(news -> ForeignNewsResponse.builder()
	// 			.id(news.getId())
	// 			.title(news.getTitle())
	// 			.description(news.getDescription())
	// 			.publishedAt(news.getPublished_at())
	// 			.imageUrl(news.getImage_url())
	// 			.url(news.getUrl())
	// 			.country(news.getCountry())
	// 			.categories(news.getCategories())
	// 			.keywords(news.getKeywords())
	// 			.build())
	// 		.collect(Collectors.toList());
	//
	// 	return ForeignNewsListResponse.builder()
	// 		.code("SUCCESS")
	// 		.success(true)
	// 		.message("요청 성공")
	// 		.data(newsList)
	// 		.build();
	// }
	//
	// public ForeignNewsListResponse searchByCategory(String category) {
	// 	List<ForeignNewsResponse> newsList = esRepository.findByCategoriesContaining(
	// 			category).stream()
	// 		.map(news -> ForeignNewsResponse.builder()
	// 			.id(news.getId())
	// 			.title(news.getTitle())
	// 			.description(news.getDescription())
	// 			.publishedAt(news.getPublished_at())
	// 			.imageUrl(news.getImage_url())
	// 			.url(news.getUrl())
	// 			.country(news.getCountry())
	// 			.categories(news.getCategories())
	// 			.keywords(news.getKeywords())
	// 			.build())
	// 		.collect(Collectors.toList());
	//
	// 	return ForeignNewsListResponse.builder()
	// 		.code("SUCCESS")
	// 		.success(true)
	// 		.message("요청 성공")
	// 		.data(newsList)
	// 		.build();
	// }

}
