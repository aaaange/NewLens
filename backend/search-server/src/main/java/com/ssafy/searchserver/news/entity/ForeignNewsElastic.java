package com.ssafy.searchserver.news.entity;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(indexName = "foreign_news")
public class ForeignNewsElastic {

	@Id
	private String id;
	private String title;
	private String description;
	private String url;
	@Field(name = "image_url")
	private String imageUrl;
	private int sentiment;

	@Field(type = FieldType.Date, name = "published_at")
	private Instant publishedAt;

	@Field(type = FieldType.Keyword)
	private List<String> categories;

	@Field(type = FieldType.Keyword)
	private String country;

	@Field(type = FieldType.Keyword)
	private List<String> keywords;

}
