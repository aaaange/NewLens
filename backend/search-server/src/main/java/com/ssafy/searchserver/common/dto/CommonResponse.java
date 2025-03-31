package com.ssafy.searchserver.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommonResponse<T> {
	private String code;
	private Boolean success;
	private String message;
	private T data;

	public static <T> CommonResponse<T> success(T data) {
		return CommonResponse.<T>builder()
			.code("SUCCESS")
			.success(true)
			.message("요청 성공")
			.data(data)
			.build();
	}

	public static <T> CommonResponse<T> fail(String code, String message) {
		return CommonResponse.<T>builder()
			.code(code)
			.success(false)
			.message(message)
			.build();
	}
}
