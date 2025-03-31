package com.ssafy.searchserver.interfaces.search.controller;

import com.ssafy.searchserver.common.exeception.CustomException;
import com.ssafy.searchserver.common.exeception.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ssafy.searchserver.common.dto.CommonResponse;
import com.ssafy.searchserver.interfaces.search.dto.KeywordRankingData;
import com.ssafy.searchserver.interfaces.search.dto.RelatedKeywordsResponse;
import com.ssafy.searchserver.application.search.SearchService;
import com.ssafy.searchserver.interfaces.search.dto.SentimentMentionData;

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
@CrossOrigin(origins = "*")
public class SearchController {

	private final SearchService service;

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

	@GetMapping("/extract_related_words")
	public ResponseEntity<CommonResponse<RelatedKeywordsResponse>> getRelatedKeywords(
		@Parameter(description = "연관어를 추출할 기준 키워드", example = "트럼프")
		@RequestParam String keyword,

		@Parameter(description = "필터링할 카테고리", example = "business")
		@RequestParam String category,

		@Parameter(description = "조회 기간 ex)1, 7, 30", example = "7")
		@RequestParam int period,

		@Parameter(description = "한국 특화 여부", example = "false")
		@RequestParam(name = "is_korea") boolean isKorea
	) {
		RelatedKeywordsResponse data = service.getRelatedKeywords(keyword, category, period, isKorea);
		CommonResponse<RelatedKeywordsResponse> response = CommonResponse.success(data);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "키워드 랭킹 조회", description = "카테고리, 기간, 국내/해외 뉴스에 따른 키워드 랭킹을 조회합니다.")
	@GetMapping("/keyword_ranking")
	public ResponseEntity<CommonResponse<KeywordRankingData>> getKeywordRanking(
		@Parameter(description = "카테고리", example = "politics") @RequestParam String category,
		@Parameter(description = "기간 (ex: 7)", example = "7") @RequestParam int period,
		@Parameter(description = "한국 필터링 여부", example = "false") @RequestParam(name = "is_korea") boolean isKorea
	) {
		KeywordRankingData data = service.getKeywordRanking(category, period, isKorea);
		CommonResponse<KeywordRankingData> response = CommonResponse.success(data);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "세계 지도 정보 출력", description = "세계 지도에서 나라별 감성 분석 결과와 언급량 결과를 출력합니다.")
	@GetMapping("/worldwide")
	public ResponseEntity<CommonResponse<SentimentMentionData>> getWorldwide(
		@Parameter(description = "키워드", example = "트럼프") @RequestParam String keyword,
		@Parameter(description = "마인드맵키워드", example = "관세") @RequestParam(name = "keyword_mind") String keywordMind,
		@Parameter(description = "카테고리", example = "politics") @RequestParam String category,
		@Parameter(description = "기간", example = "30") @RequestParam int period
	) {
		SentimentMentionData data = service.getWorldwide(keyword, keywordMind, category, period);
		CommonResponse<SentimentMentionData> response = CommonResponse.success(data);
		return ResponseEntity.ok(response);
	}
}
