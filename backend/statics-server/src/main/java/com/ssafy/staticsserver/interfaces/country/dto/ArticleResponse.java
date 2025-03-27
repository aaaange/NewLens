package com.ssafy.staticsserver.interfaces.country.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ArticleResponse {
	private String title;
	private String url;
	private LocalDateTime publishedAt;
	private String imageUrl;

	@Override
	public String toString() {
		return "ArticleResponse{" +
			"title='" + title + '\'' +
			", url='" + url + '\'' +
			", publishedAt=" + publishedAt +
			", imageUrl='" + imageUrl + '\'' +
			'}';
	}
}
