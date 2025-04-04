package com.ssafy.staticsserver.infrastructure.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class KakaoImageResponse {
	// 이미지 여러개 담을 경우 대비
	private List<Document> documents;

	@Data
	// response가 일반 문자열이 아니라 JSON으로 타입이 여러개 있어서 그럼
	public static class Document {
		@JsonProperty("image_url")
		private String imageUrl;
	}
}

