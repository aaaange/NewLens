package com.ssafy.staticsserver.interfaces.country.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MentionResponse {
	private LocalDateTime publishedAt;
	private int count;

	@Override
	public String toString() {
		return "MentionResponse{" +
			"publishedAt=" + publishedAt +
			", count=" + count +
			'}';
	}
}
