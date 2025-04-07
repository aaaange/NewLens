package com.ssafy.userserver.domain.dto;

import java.time.LocalDateTime;
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
public class NewsResponse {
	@JsonProperty("news_id")
	private String newsId;

	private String title;
	private String url;

	@JsonProperty("published_at")
	private LocalDateTime publishedAt;
	private String country;
	private List<String> keywords;

	@JsonProperty("is_scrap")
	private boolean isScrap;

	@JsonProperty("image_url")
	private String imageUrl;
}
