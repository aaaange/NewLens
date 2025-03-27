package com.ssafy.staticsserver.interfaces.country.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SentimentResponse {
	private LocalDateTime publishedAt;
	private double positive;
	private double neutral;
	private double negative;

	@Override
	public String toString() {
		return "SentimentResponse{" +
			"publishedAt=" + publishedAt +
			", positive=" + positive +
			", neutral=" + neutral +
			", negative=" + negative +
			'}';
	}
}
