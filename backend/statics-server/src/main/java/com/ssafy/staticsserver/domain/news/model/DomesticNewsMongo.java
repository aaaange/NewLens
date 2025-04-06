package com.ssafy.staticsserver.domain.news.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Document(collection = "domestic_news")
public class DomesticNewsMongo {
	@Id
	private String id;
	private String title;
	private String description;
	private String url;
	@Field(name = "published_at")
	private LocalDateTime publishedAt;
	@Field(name = "image_url")
	private String imageUrl;
	private List<String> categories;
	private String country;
	private List<String> keywords;
	private Integer sentiment;
	private String rawDataRef;

	@Override
	public String toString() {
		return "ForeignNewsMongo{" +
			"id='" + id + '\'' +
			", title='" + title + '\'' +
			", description='" + description + '\'' +
			", url='" + url + '\'' +
			", published_at=" + publishedAt +
			", image_url='" + imageUrl + '\'' +
			", categories=" + categories +
			", country='" + country + '\'' +
			", keywords=" + keywords +
			", sentiment=" + sentiment +
			", rawDataRef='" + rawDataRef + '\'' +
			'}';
	}
}

