package com.ssafy.searchserver.news.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.searchserver.common.dto.CommonErrorResponse;
import com.ssafy.searchserver.news.dto.MindMapResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/search")
@Tag(name = "Search API", description = "연관어 추출 (마인드맵) API")
@RequiredArgsConstructor
public class SearchSwaggerController {

	@Operation(
		summary = "연관어 추출",
		description = "첫번째 검색어가 입력되었을 때, 이를 기반으로 연관 키워드를 출력하는 API"
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

	@GetMapping("/extract-related-words")
	public MindMapResponse extractRelatedWords(
		@RequestHeader(name = "Authorization", required = false) String authorization,
		@Parameter(description = "카테고리", example = "sports")
		@RequestParam String category,
		@Parameter(description = "기간 (현재일 기준 며칠 전인지 ex) 1, 7, 30)", example = "7")
		@RequestParam int period,
		@Parameter(description = "검색 키워드", example = "트럼프")
		@RequestParam String keyword
	) {
		// 예시 더미 데이터
		List<String> relatedWords = List.of("관세", "도널드", "대선");

		return MindMapResponse.builder()
			.code("SUCCESS")
			.success(true)
			.message("요청 성공")
			.data(relatedWords)
			.build();
	}

}
