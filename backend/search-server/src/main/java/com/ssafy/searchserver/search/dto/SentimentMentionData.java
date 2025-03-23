package com.ssafy.searchserver.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "Sentiment 및 Mention 데이터")
public class SentimentMentionData {
	@Schema(description = "Sentiment 리스트")
	private List<SentimentResponse> sentiment;

	@Schema(description = "Mention 리스트")
	private List<MentionResponse> mention;
}
