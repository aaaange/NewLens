package com.ssafy.searchserver.domain.search.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.Getter;

@Getter
@Document(indexName = "domestic_news")
public class DomesticNewsElastic {

	@Id
	private String id;
	private String title;
	private String description;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // JSON 직렬화/ 역직렬화 처리 시 "2025-03-13T11:20:00" 형식으로 LocalDateTime을 처리
	@JsonProperty("published_at") // JSON 필드명이랑 맞출 때
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@Field(type = FieldType.Date, name = "published_at")
	private LocalDateTime publishedAt;

	@Field(type = FieldType.Keyword)
	private List<String> categories;

	@Field(type = FieldType.Keyword)
	private String country;

	@Field(type = FieldType.Keyword)
	private List<String> keywords;

	private Integer sentiment;
}
