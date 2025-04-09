package com.ssafy.staticsserver.interfaces.country.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NewsModalRequest {
	private List<String> newsIds;
	private int page;
	private int size;
	private String requestId;
	@JsonProperty("isKorea")
	private boolean isKorea;
	private String callbackUrl;

	private String keyword;
	private String keywordMind;
	private String keywordCloud;
	private String category;
	private String country;
	private int period;
	private String email;
}
