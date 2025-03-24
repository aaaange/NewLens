package com.ssafy.searchserver.interfaces.country.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "감정 데이터")
public class SentimentResponse {
	@Schema(description = "기간 (yyyy-MM-dd)", example = "2025-03-01")
	private String period;

	@Schema(description = "긍정 비율", example = "0.7")
	private double positive;

	@Schema(description = "중립 비율", example = "0.2")
	private double neutral;

	@Schema(description = "부정 비율", example = "0.1")
	private double negative;
}
