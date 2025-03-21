package com.ssafy.searchserver.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "에러 응답 Response")
public class CommonErrorResponse {
	@Schema(description = "응답 코드", example = "FAIL")
	private String code;

	@Schema(description = "성공 여부", example = "false")
	private Boolean success;

	@Schema(description = "응답 메시지", example = "요청 실패")
	private String message;

	@Schema(description = "데이터", example = "null")
	private Object data;
}
