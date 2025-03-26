package com.ssafy.staticsserver.interfaces.search.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SentimentMentionResponse {
	String keyword;
	private List<SentimentResponse> sentiment;
	private List<MentionResponse> mention;

	@Override
	public String toString() {
		return "SentimentMentionResponse{" +
			"keyword='" + keyword + '\'' +
			"sentiment=" + sentiment +
			", mention=" + mention +
			'}';
	}
}
