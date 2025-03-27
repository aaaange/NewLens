package com.ssafy.staticsserver.interfaces.country.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
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
