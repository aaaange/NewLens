package com.ssafy.searchserver.interfaces.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "Sentiment 및 Mention 데이터")
public class SentimentMentionData {
	@Schema(description = "검색 키워드")
	private String keyword;

	@Schema(description = "Sentiment 리스트")
	private List<SentimentResponse> sentiment;

	@Schema(description = "Mention 리스트")
	private List<MentionResponse> mention;
}
