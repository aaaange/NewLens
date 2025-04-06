package com.ssafy.staticsserver.interfaces.country.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class VideoResponse {
	private String title;
	private String url;
	private LocalDateTime publishedAt;
	private String thumbnailUrl;

	@Override
	public String toString() {
		return "VideoResponse{" +
				"title='" + title + '\'' +
				", url='" + url + '\'' +
				", publishedAt=" + publishedAt +
				", thumbnailUrl='" + thumbnailUrl + '\'' +
				'}';
	}
}
