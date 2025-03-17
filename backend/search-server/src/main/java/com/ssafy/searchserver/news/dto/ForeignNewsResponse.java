package com.ssafy.searchserver.news.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ForeignNewsResponse {
    private String id;
    private String title;
    private String description;
    private String url;
    private String imageUrl;
    private LocalDateTime publishedAt;
    private List<String> categories;
    private String country;
    private List<String> keywords;
    private int sentiment;

}
