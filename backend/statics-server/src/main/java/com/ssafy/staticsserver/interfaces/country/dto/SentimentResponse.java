package com.ssafy.staticsserver.interfaces.country.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
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
