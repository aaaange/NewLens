package com.ssafy.searchserver.interfaces.search.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ssafy.searchserver.common.dto.CommonResponse;
import com.ssafy.searchserver.interfaces.search.dto.ForeignNewsListResponse;
import com.ssafy.searchserver.interfaces.search.dto.ForeignNewsResponse;
import com.ssafy.searchserver.interfaces.search.dto.RelatedKeywordsResponse;
import com.ssafy.searchserver.domain.search.model.ForeignNewsElastic;
import com.ssafy.searchserver.domain.search.model.ForeignNewsMongo;
import com.ssafy.searchserver.application.search.SearchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/search")
@Tag(name = "Search API", description = "검색 및 연관어 추출 API")
@RequiredArgsConstructor
public class SearchController {

	private final SearchService service;

	// mongo 더미데이터 저장
	@Operation(summary = "MongoDB 뉴스 저장", description = "MongoDB에 뉴스 더미 데이터를 저장합니다.")
	@PostMapping("/mongo")
	public ForeignNewsMongo save(@RequestBody ForeignNewsMongo news) {
		return service.save(news);
	}

	// mongo 뉴스 리스트 조회
	@Operation(summary = "MongoDB 뉴스 리스트 조회", description = "MongoDB에 저장된 뉴스 리스트를 조회합니다.")
	@GetMapping("/mongo")
	public ResponseEntity<ForeignNewsListResponse> getMongoDBNewsList() {
		ForeignNewsListResponse response = service.getNewsList();
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	// elasticsearch 더미데이터 저장
	@Operation(summary = "Elasticsearch 뉴스 저장", description = "Elasticsearch에 뉴스 더미 데이터를 저장합니다.")
	@PostMapping("/es")
	public ForeignNewsResponse saveNews(@RequestBody ForeignNewsElastic news) {
		return service.save(news);
	}

	@Operation(
		summary = "연관어 추출",
		description = "검색 키워드를 기반으로 연관 키워드를 반환하는 API"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(
			mediaType = "application/json",
			examples = {
				@ExampleObject(name = "INVALID_CATEGORY", summary = "유효하지 않은 카테고리", value = "{\"code\": \"INVALID_CATEGORY\", \"success\": false, \"message\": \"유효하지 않은 카테고리입니다\", \"data\": null}"),
				@ExampleObject(name = "INVALID_PERIOD", summary = "유효하지 않은 기간", value = "{\"code\": \"INVALID_PERIOD\", \"success\": false, \"message\": \"유효하지 않은 기간 설정입니다\", \"data\": null}"),
				@ExampleObject(name = "INVALID_KEYWORD", summary = "유효하지 않은 키워드", value = "{\"code\": \"INVALID_KEYWORD\", \"success\": false, \"message\": \"유효하지 않은 키워드입니다\", \"data\": null}")
			}
		)),
		@ApiResponse(responseCode = "401", description = "Authorization 헤더 없음", content = @Content(
			mediaType = "application/json",
			examples = @ExampleObject(name = "UNAUTHORIZED", summary = "인증 실패", value = "{\"code\": \"UNAUTHORIZED\", \"success\": false, \"message\": \"Authorization 헤더가 없습니다\", \"data\": null}")
		)),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(
			mediaType = "application/json",
			examples = @ExampleObject(name = "SERVER_ERROR", summary = "서버 오류 발생", value = "{\"code\": \"SERVER_ERROR\", \"success\": false, \"message\": \"서버 내부 오류가 발생했습니다\", \"data\": null}")
		))
	})


	@GetMapping("/extract-related_words")
	public CommonResponse<RelatedKeywordsResponse> getRelatedKeywords(
		@Parameter(description = "연관어를 추출할 기준 키워드", example = "트럼프")
		@RequestParam String keyword,

		@Parameter(description = "필터링할 카테고리", example = "business")
		@RequestParam String category,

		@Parameter(description = "조회 기간 ex)1, 7, 30", example = "7")
		@RequestParam(defaultValue = "7") int period
	) {
		RelatedKeywordsResponse data = service.getRelatedKeywords(keyword, category, period);
		return CommonResponse.success(data);
	}


	@Operation(summary = "키워드 랭킹 조회", description = "카테고리, 기간, 국내/해외 뉴스에 따른 키워드 랭킹을 조회합니다.")
	@GetMapping("/keyword-ranking")
	public CommonResponse<ForeignNewsListResponse> getKeywordRanking(
		@Parameter(description = "카테고리", example = "politics") @RequestParam String category,
		@Parameter(description = "기간 (ex: 7)", example = "7") @RequestParam int period,
		@Parameter(description = "한국 필터링 여부", example = "true") @RequestParam boolean is_korea
	) {
		ForeignNewsListResponse response = service.getKeywordRanking(category, period, is_korea);
		return CommonResponse.success(response);
	}


	@Operation(summary = "세계 지도 정보 출력", description = "세계 지도에서 나라별 감성 분석 결과와 언급량 결과를 출력합니다.")
	@GetMapping("/worldwide")
	public CommonResponse<ForeignNewsListResponse> getWorldwide(
		@Parameter(description = "키워드", example = "바이든") @RequestParam String keyword,
		@Parameter(description = "카테고리", example = "politics") @RequestParam String category,
		@Parameter(description = "기간", example = "30") @RequestParam int period
	) {
		ForeignNewsListResponse response = service.getWorldwide(keyword, category, period);
		return CommonResponse.success(response);
	}
}
