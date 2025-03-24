package com.ssafy.searchserver.interfaces.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Mention 응답 데이터")
public class MentionResponse {
	@Schema(description = "국가 코드", example = "ko")
	private String name;

	@Schema(description = "언급 수", example = "125")
	private int count;
}
