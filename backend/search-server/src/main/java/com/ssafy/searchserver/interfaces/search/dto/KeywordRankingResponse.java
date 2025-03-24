package com.ssafy.searchserver.interfaces.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "키워드 랭킹 Response")
public class KeywordRankingResponse {
	@Schema(description = "응답 코드", example = "SUCCESS")
	private String code;

	@Schema(description = "성공 여부", example = "true")
	private Boolean success;

	@Schema(description = "응답 메시지", example = "요청 성공")
	private String message;

	@Schema(description = "키워드 랭킹 리스트")
	private KeywordRankingData data;
}
