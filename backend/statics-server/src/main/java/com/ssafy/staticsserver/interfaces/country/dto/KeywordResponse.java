package com.ssafy.staticsserver.interfaces.country.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KeywordResponse {
	private String name;
	private long count;

	@Override
	public String toString() {
		return "KeywordResponse{" +
			"name='" + name + '\'' +
			", count=" + count +
			'}';
	}
}
