package com.ssafy.searchserver.search.country.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "언급량 데이터")
public class MentionResponse {
	@Schema(description = "기간 (yyyy-MM-dd)", example = "2025-03-01")
	private String period;

	@Schema(description = "언급 횟수", example = "121")
	private int count;
}
