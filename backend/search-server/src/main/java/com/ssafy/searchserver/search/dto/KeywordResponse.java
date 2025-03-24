package com.ssafy.searchserver.search.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KeywordResponse {
	private String name;
	private String state;
}
