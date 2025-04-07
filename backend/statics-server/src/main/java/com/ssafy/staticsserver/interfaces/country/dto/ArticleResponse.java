package com.ssafy.staticsserver.interfaces.country.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ArticleResponse {
	private String title;
	private String url;
	private LocalDateTime publishedAt;
	private String imageUrl;
	private String originTitle;

	@Override
	public String toString() {
		return "ArticleResponse{" +
				"title='" + title + '\'' +
				", url='" + url + '\'' +
				", publishedAt=" + publishedAt +
				", imageUrl='" + imageUrl + '\'' +
				", originTitle='" + originTitle + '\'' +
				'}';
	}
}
