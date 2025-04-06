package com.ssafy.staticsserver.interfaces.country.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardData {
	private List<KeywordResponse> wordcloud;
	private List<SentimentResponse> sentiment;
	private List<MentionResponse> mentions;
	private List<ArticleResponse> articles;
	private List<VideoResponse> videos;

	@Override
	public String toString() {
		return "DashboardData{" +
			"wordcloud=" + wordcloud +
			", sentiment=" + sentiment +
			", mentions=" + mentions +
			", articles=" + articles +
			", videos=" + videos +
			'}';
	}
}
