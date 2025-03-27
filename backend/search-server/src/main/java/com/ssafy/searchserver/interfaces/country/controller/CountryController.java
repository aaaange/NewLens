package com.ssafy.searchserver.interfaces.country.controller;

import java.time.LocalDateTime;
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
	public CommonResponse<DashboardData> extractDashboard(
		@RequestHeader(name = "Authorization", required = false) String authorization,
		@Parameter(description = "카테고리", example = "sports") @RequestParam String category,
		@Parameter(description = "기간 (현재일 기준 며칠 전인지 ex) 1, 7, 30)", example = "7") @RequestParam int period,
		@Parameter(description = "검색 키워드", example = "트럼프") @RequestParam String keyword,
		@Parameter(description = "마인드맵 키워드", example = "관세") @RequestParam(name = "keyword-mind") String keywordMind,
		@Parameter(description = "클라우드 키워드", example = "도널드") @RequestParam(name = "keyword-cloud") String keywordCloud,
		@Parameter(description = "국가", example = "ko") @RequestParam String country,
		@Parameter(description = "한국 여부", example = "false") @RequestParam(name = "is_korea") boolean isKorea
	) {
		DashboardData data = countryService.getDashboard(category, period, keyword, keywordMind, keywordCloud, country, isKorea);
		return CommonResponse.success(data);
	}

	@GetMapping("/compare-info")
	public CompareInfoResponse extractCompareInfo(
		@RequestHeader(name = "Authorization", required = false) String authorization,
		@Parameter(description = "카테고리", example = "sports") @RequestParam String category,
		@Parameter(description = "기간 (현재일 기준 며칠 전인지 ex) 1, 7, 30)", example = "7") @RequestParam int period,
		@Parameter(description = "검색 키워드", example = "트럼프") @RequestParam String keyword,
		@Parameter(description = "마인드맵 키워드", example = "관세") @RequestParam(name = "keyword-mind") String keywordMind,
		@Parameter(description = "국가1", example = "ko") @RequestParam String country1,
		@Parameter(description = "국가2", example = "us") @RequestParam String country2
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
		@Parameter(description = "카테고리", example = "sports") @RequestParam String category,
		@Parameter(description = "기간 (현재일 기준 며칠 전인지 ex) 1, 7, 30)", example = "7") @RequestParam int period,
		@Parameter(description = "검색 키워드", example = "트럼프") @RequestParam String keyword,
		@Parameter(description = "마인드맵 키워드", example = "관세") @RequestParam(name = "keyword-mind") String keywordMind,
		@Parameter(description = "클라우드 키워드", example = "정책") @RequestParam(name = "keyword-cloud") String keywordCloud,
		@Parameter(description = "국가", example = "ko") @RequestParam String country,
		@Parameter(description = "페이지", example = "1") @RequestParam int page,
		@Parameter(description = "페이지 당 보여줄 개수", example = "5") @RequestParam int size,
		@Parameter(description = "한국 특화 여부", example = "false") @RequestParam(name = "is_korea") boolean isKorea

	) {
		NewsModalResponse data = countryService.getNewsNodal(category, period, keyword, keywordMind, keywordCloud, country, page, size, isKorea);
		return CommonResponse.success(data);
	}

}
