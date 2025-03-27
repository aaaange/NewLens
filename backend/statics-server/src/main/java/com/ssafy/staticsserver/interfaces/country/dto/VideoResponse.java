package com.ssafy.staticsserver.interfaces.country.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
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
