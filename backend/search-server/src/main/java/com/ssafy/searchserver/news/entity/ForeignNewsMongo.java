package com.ssafy.searchserver.news.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "foreign_news")
public class ForeignNewsMongo {
    @Id
    private String id;
    private String title;
    private String description;
    private String url;
    @Field(name = "published_at")
    private LocalDateTime published_at;
    @Field(name = "image_url")
    private String image_url;
    private List<String> categories;
    private String country;
    private List<String> keywords;
    private Integer sentiment;
    private String rawDataRef;

}
