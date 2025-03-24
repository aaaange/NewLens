package com.ssafy.searchserver.search.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ssafy.searchserver.search.dto.ForeignNewsListResponse;
import com.ssafy.searchserver.search.dto.ForeignNewsResponse;
import com.ssafy.searchserver.search.dto.MindMapResponse;
import com.ssafy.searchserver.search.entity.ForeignNewsElastic;
import com.ssafy.searchserver.search.entity.ForeignNewsMongo;
import com.ssafy.searchserver.search.service.SearchServiceImpl;

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

	private final SearchServiceImpl service;

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

	// 연관어 추출 API (마인드맵)
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
	@GetMapping("/extract-related-words")
	public MindMapResponse extractRelatedWords(
		@RequestHeader(name = "Authorization", required = false) String authorization,
		@Parameter(description = "카테고리", example = "sports") @RequestParam String category,
		@Parameter(description = "기간 (현재일 기준 며칠 전인지 ex) 1, 7, 30)", example = "7") @RequestParam int period,
		@Parameter(description = "검색 키워드", example = "트럼프") @RequestParam String keyword
	) {
		// 예시 더미 데이터
		List<String> relatedWords = List.of("관세", "도널드", "대선");

		return MindMapResponse.builder()
			.code("SUCCESS")
			.success(true)
			.message("요청 성공")
			.data(Map.of("keywords", relatedWords))
			.build();
	}


	// 키워드 랭킹
	@Operation(summary = "키워드 랭킹 조회", description = "카테고리, 기간에 따른 키워드 랭킹을 조회합니다.")
	@GetMapping("/keyword-ranking")
	public ResponseEntity<ForeignNewsListResponse> getKeywordRanking(
		@Parameter(description = "카테고리", example = "politics") @RequestParam String category,
		@Parameter(description = "기간 (ex: 7)", example = "7") @RequestParam int period,
		@Parameter(description = "한국 필터링 여부", example = "true") @RequestParam boolean is_korea
	) {
		ForeignNewsListResponse response = service.getKeywordRanking(category, period, is_korea);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	// 글로벌 키워드 분석
	@Operation(summary = "글로벌 키워드 분석", description = "세계적인 뉴스 속 해당 키워드와 연관된 데이터를 조회합니다.")
	@GetMapping("/worldwide")
	public ResponseEntity<ForeignNewsListResponse> getWorldwide(
		@Parameter(description = "키워드", example = "바이든") @RequestParam String keyword,
		@Parameter(description = "카테고리", example = "politics") @RequestParam String category,
		@Parameter(description = "기간", example = "30") @RequestParam int period
	) {
		ForeignNewsListResponse response = service.getWorldwide(keyword, category, period);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
