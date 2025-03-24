package com.ssafy.searchserver.search.country.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
@Schema(description = "뉴스 데이터")
public class NewsData {
	@Schema(description = "뉴스 목록")
	private List<NewsResponse> news;

	@Schema(description = "현재 페이지", example = "1")
	private int page;

	@Schema(description = "페이지 크기", example = "10")
	private int size;

	@Schema(description = "전체 요소 수", example = "1024")
	private int totalElements;

	@Schema(description = "전체 페이지 수", example = "103")
	private int totalPages;

	@Schema(description = "다음 페이지 여부", example = "true")
	private boolean hasNext;

	@Schema(description = "이전 페이지 여부", example = "false")
	private boolean hasPrevious;
}
