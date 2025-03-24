package com.ssafy.searchserver.interfaces.search.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "키워드 랭킹 데이터")
public class KeywordRankingData {
	@Schema(description = "키워드 리스트")
	private List<KeywordResponse> keywords;
}

