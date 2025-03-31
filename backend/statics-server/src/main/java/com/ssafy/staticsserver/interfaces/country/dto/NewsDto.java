package com.ssafy.staticsserver.interfaces.country.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NewsDto {
	private String title;
	private String url;
	private LocalDateTime publishedAt;
	private String imageUrl;
	private List<String> keywords;

	@Override
	public String toString() {
		return "NewsDto{" +
			"title='" + title + '\'' +
			", url='" + url + '\'' +
			", publishedAt=" + publishedAt +
			", imageUrl='" + imageUrl + '\'' +
			", keywords=" + keywords +
			'}';
	}
}
