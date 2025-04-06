package com.ssafy.searchserver.interfaces.country.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "키워드 정보")
public class KeywordResponse {
	@Schema(description = "키워드명", example = "트럼프")
	private String name;

	@Schema(description = "카운트 수", example = "121")
	private long count;

	@Override
	public String toString() {
		return "KeywordResponse{" +
			"name='" + name + '\'' +
			", count=" + count +
			'}';
	}
}
