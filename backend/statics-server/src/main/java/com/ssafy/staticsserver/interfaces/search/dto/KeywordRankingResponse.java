package com.ssafy.staticsserver.interfaces.search.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeywordRankingResponse {
	private List<KeywordRankingDto> keywords;
}