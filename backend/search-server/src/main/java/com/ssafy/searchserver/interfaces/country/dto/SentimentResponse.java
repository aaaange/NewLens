package com.ssafy.searchserver.interfaces.country.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "감정 데이터")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SentimentResponse {
	@Schema(description = "기간 (yyyy-MM-dd)", example = "2025-03-01")
	private LocalDateTime publishedAt;

	@Schema(description = "긍정 비율", example = "0.7")
	private double positive;

	@Schema(description = "중립 비율", example = "0.2")
	private double neutral;

	@Schema(description = "부정 비율", example = "0.1")
	private double negative;
}
