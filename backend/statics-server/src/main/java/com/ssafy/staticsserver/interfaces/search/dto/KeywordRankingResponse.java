package com.ssafy.staticsserver.interfaces.search.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KeywordRankingResponse {
	private List<KeywordRankingDto> keywords;
}