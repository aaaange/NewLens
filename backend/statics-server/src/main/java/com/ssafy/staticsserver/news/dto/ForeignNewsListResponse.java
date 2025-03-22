package com.ssafy.staticsserver.news.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ForeignNewsListResponse {
    private String code;
    private boolean success;
    private String message;
    private List<ForeignNewsResponse> data;
}
