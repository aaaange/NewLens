package com.ssafy.staticsserver.interfaces.country.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardData {
	private List<KeywordResponse> keywords;
	private String description;
	private List<SentimentResponse> sentiment;
	private List<MentionResponse> mentions;
	private List<ArticleResponse> articles;
	private List<VideoResponse> videos;

	@Override
	public String toString() {
		return "DashboardData{" +
			"keywords=" + keywords +
			", description='" + description + '\'' +
			", sentiment=" + sentiment +
			", mentions=" + mentions +
			", articles=" + articles +
			", videos=" + videos +
			'}';
	}
}
