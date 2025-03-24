package com.ssafy.searchserver.search.country.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "영상 데이터")
public class VideoResponse {
	@Schema(description = "영상 제목", example = "AI가 바꿀 미래, 우리는 어떻게 준비해야 할까?")
	private String title;

	@Schema(description = "영상 URL", example = "https://www.youtube.com/watch?v=abcd1234")
	private String url;

	@Schema(description = "게시일 (yyyy-MM-dd)", example = "2025-03-11")
	private String publishedDate;

	@Schema(description = "썸네일 URL", example = "https://img.youtube.com/vi/abcd1234/maxresdefault.jpg")
	private String thumbnailUrl;
}
