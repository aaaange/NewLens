package com.ssafy.searchserver.news_es.entity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.util.List;
@Getter
@Setter
@Document(indexName = "foreign_news")
public class ForeignNews {

    @Id
    private String id;

    private String title;

    private String description;

    private String url;

    private String image_url;

    @Field(type = FieldType.Date)
    private Instant published_at;

    @Field(type = FieldType.Keyword)
    private List<String> categories;

    private String country;

    @Field(type = FieldType.Keyword)
    private List<String> keywords;

    private int sentiment;

}
