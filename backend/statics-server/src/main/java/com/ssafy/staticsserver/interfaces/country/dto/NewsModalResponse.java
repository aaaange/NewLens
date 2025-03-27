package com.ssafy.staticsserver.interfaces.country.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NewsModalResponse {
	private List<NewsDto> news;
	private int page;
	private int size;
	private int totalElements;
	private int totalPages;
	private boolean hasNext;
	private boolean hasPrevious;

	@Override
	public String toString() {
		return "NewsModalResponse{" +
			"news=" + news +
			", page=" + page +
			", size=" + size +
			", totalElements=" + totalElements +
			", totalPages=" + totalPages +
			", hasNext=" + hasNext +
			", hasPrevious=" + hasPrevious +
			'}';
	}
}
