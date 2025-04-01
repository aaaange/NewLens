package com.ssafy.staticsserver.interfaces.search.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Builder
public class SentimentMentionResponse {
	String keyword;
	@JsonProperty("keyword_mind")
	String keywordMind;
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
