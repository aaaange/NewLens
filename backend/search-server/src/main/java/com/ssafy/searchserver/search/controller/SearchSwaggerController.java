package com.ssafy.searchserver.search.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.searchserver.search.dto.KeywordRankingData;
import com.ssafy.searchserver.search.dto.KeywordRankingResponse;
import com.ssafy.searchserver.search.dto.KeywordResponse;
import com.ssafy.searchserver.search.dto.MentionResponse;
import com.ssafy.searchserver.search.dto.MindMapResponse;
import com.ssafy.searchserver.search.dto.SentimentMentionData;
import com.ssafy.searchserver.search.dto.SentimentMentionResponse;
import com.ssafy.searchserver.search.dto.SentimentResponse;

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

	@GetMapping("/keyword-ranking")
	public KeywordRankingResponse extractKeywordRanking(
		@RequestHeader(name = "Authorization", required = false) String authorization,
		@Parameter(description = "카테고리", example = "sports")
		@RequestParam String category,
		@Parameter(description = "기간 (현재일 기준 며칠 전인지 ex) 1, 7, 30)", example = "7")
		@RequestParam int period,
		@Parameter(description = "한국 여부", example = "false")
		@RequestParam boolean isKorea
	) {
		// 예시 더미 데이터
		List<KeywordResponse> keywordRanking = List.of(
			KeywordResponse.builder().name("도널드").state("new").build(),
			KeywordResponse.builder().name("덕덕").state("hot").build(),
			KeywordResponse.builder().name("트럼프").state("").build()
		);

		KeywordRankingData keywordRankingData = KeywordRankingData.builder()
			.keywords(keywordRanking)
			.build();

		return KeywordRankingResponse.builder()
			.code("SUCCESS")
			.success(true)
			.message("요청 성공")
			.data(keywordRankingData)
			.build();
	}

	@GetMapping("/worldwide")
	public SentimentMentionResponse extractSentimentMention(
		@RequestHeader(name = "Authorization", required = false) String authorization,
		@Parameter(description = "카테고리", example = "sports")
		@RequestParam String category,
		@Parameter(description = "기간 (현재일 기준 며칠 전인지 ex) 1, 7, 30)", example = "7")
		@RequestParam int period,
		@Parameter(description = "검색 키워드", example = "트럼프")
		@RequestParam String keyword
	) {
		// 예시 더미 데이터
		// Sentiment 더미 데이터
		List<SentimentResponse> sentimentList = List.of(
			SentimentResponse.builder().name("ko").positive(0.7).neutral(0.2).negative(0.1).build(),
			SentimentResponse.builder().name("us").positive(0.3).neutral(0.5).negative(0.2).build()
		);

		// Mention 더미 데이터
		List<MentionResponse> mentionList = List.of(
			MentionResponse.builder().name("ko").count(125).build(),
			MentionResponse.builder().name("us").count(119).build()
		);

		// 데이터를 하나의 객체로 묶기
		SentimentMentionData data = SentimentMentionData.builder()
			.sentiment(sentimentList)
			.mention(mentionList)
			.build();

		return SentimentMentionResponse.builder()
			.code("SUCCESS")
			.success(true)
			.message("요청 성공")
			.data(data)
			.build();
	}

}
