package com.ssafy.searchserver.interfaces.country.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "분석 데이터")
public class AnalysisData {
	@Schema(description = "분석 내용", example = "한줄 비교 요약본 from gpt")
	private String analysis;
}
