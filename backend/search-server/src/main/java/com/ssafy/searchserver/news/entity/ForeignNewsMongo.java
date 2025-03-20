package com.ssafy.searchserver.news.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
    private LocalDateTime publishedAt;
    private String imageUrl;
    private List<String> categories;
    private String country;
    private List<String> keywords;
    private Integer sentiment;
    private String rawDataRef;

}
