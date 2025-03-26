package com.ssafy.staticsserver.search.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SentimentResponse {
	private String country;
	private double positive;
	private double neutral;
	private double negative;

	@Override
	public String toString() {
		return "SentimentResponse{" +
			", country='" + country + '\'' +
			", positive=" + positive +
			", neutral=" + neutral +
			", negative=" + negative +
			'}';
	}
}
