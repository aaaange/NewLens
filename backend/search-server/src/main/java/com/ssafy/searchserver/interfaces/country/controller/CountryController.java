package com.ssafy.searchserver.interfaces.country.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.searchserver.application.country.CountryService;
import com.ssafy.searchserver.common.dto.CommonResponse;
import com.ssafy.searchserver.interfaces.country.dto.AnalysisData;
import com.ssafy.searchserver.interfaces.country.dto.ArticleResponse;
import com.ssafy.searchserver.interfaces.country.dto.CompareInfoResponse;
import com.ssafy.searchserver.interfaces.country.dto.DashboardData;
import com.ssafy.searchserver.interfaces.country.dto.DashboardResponse;
import com.ssafy.searchserver.interfaces.country.dto.KeywordResponse;
import com.ssafy.searchserver.interfaces.country.dto.MentionResponse;
import com.ssafy.searchserver.interfaces.country.dto.NewsData;
import com.ssafy.searchserver.interfaces.country.dto.NewsModalResponse;
import com.ssafy.searchserver.interfaces.country.dto.NewsResponse;
import com.ssafy.searchserver.interfaces.country.dto.SearchNewsResponse;
import com.ssafy.searchserver.interfaces.country.dto.SentimentResponse;
import com.ssafy.searchserver.interfaces.country.dto.VideoResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/search/country")
@Tag(name = "Country API", description = "국가 대시보드 추출 API")
@RequiredArgsConstructor
public class CountryController {

	private final CountryService countryService;

	@Operation(
		summary = "국가 대시보드 추출",
		description = "국가를 선택했을 때, 해당 국가의 상세 대시보드를 출력하는 API"
	)
	@ApiResponses({
		// 400 에러 응답
		@ApiResponse(
			responseCode = "400",
			description = "잘못된 요청",
			content = @Content(
				mediaType = "application/json",
				examples = {
					@ExampleObject(
						name = "INVALID_CATEGORY",
						summary = "유효하지 않은 카테고리",
						value = """
							{
							    "code": "INVALID_CATEGORY",
							    "success": false,
							    "message": "유효하지 않은 카테고리입니다",
							    "data": null
							}
							"""
					),
					@ExampleObject(
						name = "INVALID_PERIOD",
						summary = "유효하지 않은 기간",
						value = """
							{
							    "code": "INVALID_PERIOD",
							    "success": false,
							    "message": "유효하지 않은 기간 설정입니다",
							    "data": null
							}
							"""
					),
					@ExampleObject(
						name = "INVALID_KEYWORD",
						summary = "유효하지 않은 키워드",
						value = """
							{
							    "code": "INVALID_KEYWORD",
							    "success": false,
							    "message": "유효하지 않은 키워드입니다",
							    "data": null
							}
							"""
					)
				}
			)
		),

		// 401 에러 응답
		@ApiResponse(
			responseCode = "401",
			description = "Authorization 헤더 없음",
			content = @Content(
				mediaType = "application/json",
				examples = @ExampleObject(
					name = "UNAUTHORIZED",
					summary = "인증 실패",
					value = """
						{
						    "code": "UNAUTHORIZED",
						    "success": false,
						    "message": "Authorization 헤더가 없습니다",
						    "data": null
						}
						"""
				)
			)
		),

		// 500 에러 응답
		@ApiResponse(
			responseCode = "500",
			description = "서버 내부 오류",
			content = @Content(
				mediaType = "application/json",
				examples = @ExampleObject(
					name = "SERVER_ERROR",
					summary = "서버 오류 발생",
					value = """
						{
						    "code": "SERVER_ERROR",
						    "success": false,
						    "message": "서버 내부 오류가 발생했습니다",
						    "data": null
						}
						"""
				)
			)
		)
	})

	@GetMapping("/dashboard")
	public DashboardResponse extractDashboard(
		@RequestHeader(name = "Authorization", required = false) String authorization,
		@Parameter(description = "카테고리", example = "sports")
		@RequestParam String category,
		@Parameter(description = "기간 (현재일 기준 며칠 전인지 ex) 1, 7, 30)", example = "7")
		@RequestParam int period,
		@Parameter(description = "검색 키워드", example = "트럼프, 관세")
		@RequestParam String keyword,
		@RequestParam(name = "keyword-mind") String keywordMind,
		@RequestParam(name = "keyword-cloud") String keywordCloud,
		@Parameter(description = "국가", example = "ko")
		@RequestParam String country,
		@Parameter(description = "한국 여부", example = "false")
		@RequestParam boolean is_korea
	) {
		countryService.getDashboard(category, period, keyword, keywordMind, keywordCloud, country, is_korea);
		// 예시 더미 데이터
		// 1) keywords
		List<KeywordResponse> keywords = List.of(
			KeywordResponse.builder().name("트럼프").count(121).build(),
			KeywordResponse.builder().name("관세").count(121).build()
		);

		// 2) description
		String description = "3줄 요약편\n2줄...\n1줄...";

		// 3) sentiment
		List<SentimentResponse> sentiment = List.of(
			SentimentResponse.builder().period("2025-03-01").positive(0.7).neutral(0.2).negative(0.1).build(),
			SentimentResponse.builder().period("2025-03-02").positive(0.7).neutral(0.2).negative(0.1).build()
		);

		// 4) mentions
		List<MentionResponse> mentions = List.of(
			MentionResponse.builder().period("2025-03-01").count(121).build(),
			MentionResponse.builder().period("2025-03-02").count(126).build()
		);

		// 5) articles
		List<ArticleResponse> articles = List.of(
			ArticleResponse.builder()
				.title("AI 기술의 발전과 미래")
				.url("https://example.com/article1")
				.publishedDate("2025-03-11")
				.imageUrl("https://example.com/images/article1.jpg")
				.build(),
			ArticleResponse.builder()
				.title("챗봇이 바꾸는 고객 서비스")
				.url("https://example.com/article2")
				.publishedDate("2025-03-10")
				.imageUrl("https://example.com/images/article2.jpg")
				.build()
		);

		// 6) videos
		List<VideoResponse> videos = List.of(
			VideoResponse.builder()
				.title("AI가 바꿀 미래, 우리는 어떻게 준비해야 할까?")
				.url("https://www.youtube.com/watch?v=abcd1234")
				.publishedDate("2025-03-11")
				.thumbnailUrl("https://img.youtube.com/vi/abcd1234/maxresdefault.jpg")
				.build(),
			VideoResponse.builder()
				.title("챗봇 기술의 발전과 전망")
				.url("https://www.youtube.com/watch?v=efgh5678")
				.publishedDate("2025-03-10")
				.thumbnailUrl("[https://img.youtube.com/vi/efgh5678/maxresdefault.jpg")
				.build()
		);

		// data DTO 구성
		DashboardData data = DashboardData.builder()
			.keywords(keywords)
			.description(description)
			.sentiment(sentiment)
			.mentions(mentions)
			.articles(articles)
			.videos(videos)
			.build();

		return DashboardResponse.builder()
			.code("SUCCESS")
			.success(true)
			.message("요청 성공")
			.data(data)
			.build();
	}

	@GetMapping("/compare-info")
	public CompareInfoResponse extractCompareInfo(
		@RequestHeader(name = "Authorization", required = false) String authorization,
		@Parameter(description = "카테고리", example = "sports")
		@RequestParam String category,
		@Parameter(description = "기간 (현재일 기준 며칠 전인지 ex) 1, 7, 30)", example = "7")
		@RequestParam int period,
		@Parameter(description = "검색 키워드", example = "트럼프")
		@RequestParam String keyword,
		@Parameter(description = "마인드맵 키워드", example = "관세")
		@RequestParam(name = "keyword-mind") String keywordMind,
		@Parameter(description = "국가1", example = "ko")
		@RequestParam String country1,
		@Parameter(description = "국가2", example = "us")
		@RequestParam String country2
	) {
		return countryService.getCompareInfo(category, period, keyword, keywordMind, country1, country2);
		// 예시 더미 데이터

		//		AnalysisData data = AnalysisData.builder()
		//			.analysis("한줄 비교 요약본 from gpt")
		//			.build();
		//
		//		return CompareInfoResponse.builder()
		//			.code("SUCCESS")
		//			.success(true)
		//			.message("요청 성공")
		//			.data(data)
		//			.build();
	}

	@GetMapping("/news")
	public CommonResponse<NewsModalResponse> getNewsModal(
		@RequestHeader(name = "Authorization", required = false) String authorization,
		@Parameter(description = "카테고리", example = "sports")
		@RequestParam String category,
		@Parameter(description = "기간 (현재일 기준 며칠 전인지 ex) 1, 7, 30)", example = "7")
		@RequestParam int period,
		@Parameter(description = "검색 키워드", example = "트럼프")
		@RequestParam String keyword,
		@Parameter(description = "마인드맵 키워드", example = "관세")
		@RequestParam(name = "keyword-mind") String keywordMind,
		@Parameter(description = "클라우드 키워드", example = "정책")
		@RequestParam(name = "keyword-cloud") String keywordCloud,
		@Parameter(description = "국가", example = "ko")
		@RequestParam String country,
		@Parameter(description = "페이지", example = "1")
		@RequestParam int page,
		@Parameter(description = "페이지 당 보여줄 개수", example = "5")
		@RequestParam int size,
		@Parameter(description = "한국 특화 여부", example = "false")
		@RequestParam(name = "is_korea") boolean isKorea

	) {
		NewsModalResponse data = countryService.getNewsNodal(category, period, keyword, keywordMind, keywordCloud, country, page, size, isKorea);

		return CommonResponse.success(data);
	}

}
