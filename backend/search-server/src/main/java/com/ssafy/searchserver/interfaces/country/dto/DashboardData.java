package com.ssafy.searchserver.interfaces.country.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "검색 상세 데이터")
public class DashboardData {

	@Schema(description = "키워드 리스트")
	private List<KeywordResponse> wordcloud;

	@Schema(description = "3줄 요약")
	private String description;

	@Schema(description = "감정 데이터 목록")
	private List<SentimentResponse> sentiment;

	@Schema(description = "언급량 데이터 목록")
	private List<MentionResponse> mentions;

	@Schema(description = "기사 목록")
	private List<ArticleResponse> articles;

	@Schema(description = "영상 목록")
	private List<VideoResponse> videos;
}
