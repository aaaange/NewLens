package com.ssafy.searchserver.interfaces.country.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "언급량 데이터")
public class MentionResponse {
	@Schema(description = "기간 (yyyy-MM-dd)", example = "2025-03-01")
	private LocalDateTime publishedAt;

	@Schema(description = "언급 횟수", example = "121")
	private int count;
}
