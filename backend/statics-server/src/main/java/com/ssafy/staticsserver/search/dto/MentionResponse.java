package com.ssafy.staticsserver.search.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MentionResponse {
	private String keyword;
	private String country;
	private int count;

	@Override
	public String toString() {
		return "MentionResponse{" +
			"keyword='" + keyword + '\'' +
			", country='" + country + '\'' +
			", count=" + count +
			'}';
	}
}
