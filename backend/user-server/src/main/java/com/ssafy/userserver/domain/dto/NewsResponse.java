package com.ssafy.userserver.domain.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewsResponse {
	private String newsId;
	private String title;
	private String url;
	private LocalDateTime publishedAt;
	private String country;
	private List<String> keywords;
	private boolean isScrap;
	private String imageUrl;
}
