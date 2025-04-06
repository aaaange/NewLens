package com.ssafy.searchserver.interfaces.country.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "검색 분석 응답")
public class CompareInfoResponse {
	@Schema(description = "분석 데이터")
	private AnalysisData data;
}
