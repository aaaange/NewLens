package com.ssafy.staticsserver.interfaces.country.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NewsDto {
	private String title;
	private String url;
	private LocalDateTime publishedAt;
	private String imageUrl;
	private int sentiment;
	private List<String> keywords;
}
