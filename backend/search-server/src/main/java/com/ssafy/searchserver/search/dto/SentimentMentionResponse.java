package com.ssafy.searchserver.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Sentiment 및 Mention Response")
public class SentimentMentionResponse {
	@Schema(description = "응답 코드", example = "SUCCESS")
	private String code;

	@Schema(description = "성공 여부", example = "true")
	private Boolean success;

	@Schema(description = "응답 메시지", example = "요청 성공")
	private String message;

	@Schema(description = "데이터")
	private SentimentMentionData data;
}
