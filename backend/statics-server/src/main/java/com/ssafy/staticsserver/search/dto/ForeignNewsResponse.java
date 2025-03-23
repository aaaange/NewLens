package com.ssafy.staticsserver.search.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

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
    private Integer sentiment;
    private String rawDataRef;

}
