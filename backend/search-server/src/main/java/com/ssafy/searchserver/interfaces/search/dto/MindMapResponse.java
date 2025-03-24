package com.ssafy.searchserver.interfaces.search.dto;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "연관어 응답 Response")
public class MindMapResponse {
	@Schema(description = "응답 코드", example = "SUCCESS")
	private String code;

	@Schema(description = "성공 여부", example = "true")
	private Boolean success;

	@Schema(description = "응답 메시지", example = "요청 성공")
	private String message;

	@Schema(description = "연관 키워드 리스트", example = "keywords: [\"관세\", \"도널드\", \"머스크\"]")
	private Map<String, List<String>> data;
}
