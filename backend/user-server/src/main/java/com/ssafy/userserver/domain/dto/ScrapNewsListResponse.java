package com.ssafy.userserver.domain.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScrapNewsListResponse {
	private List<NewsResponse> news;
	private int page;
	private int size;

	@JsonProperty("total_elements")
	private long totalElements;

	@JsonProperty("total_pages")
	private int totalPages;

	@JsonProperty("has_next")
	private boolean hasNext;

	@JsonProperty("has_previous")
	private boolean hasPrevious;
}
