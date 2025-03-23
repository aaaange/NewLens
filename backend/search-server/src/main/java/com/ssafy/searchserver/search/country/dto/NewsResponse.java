package com.ssafy.searchserver.search.country.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "뉴스 항목 데이터")
public class NewsResponse {
	@Schema(description = "뉴스 제목", example = "AI 기술의 발전과 미래")
	private String title;

	@Schema(description = "뉴스 URL", example = "https://example.com/article1")
	private String url;

	@Schema(description = "게시일 (yyyy-MM-dd)", example = "2025-03-11")
	private String publishedDate;

	@Schema(description = "이미지 URL", example = "https://example.com/images/article1.jpg")
	private String imageUrl;
}
