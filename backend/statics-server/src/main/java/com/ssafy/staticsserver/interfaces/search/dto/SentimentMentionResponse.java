package com.ssafy.staticsserver.interfaces.search.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SentimentMentionResponse {
	private List<SentimentResponse> sentiment;
	private List<MentionResponse> mention;

	@Override
	public String toString() {
		return "SentimentMentionResponse{" +
			"sentiment=" + sentiment +
			", mention=" + mention +
			'}';
	}
}
