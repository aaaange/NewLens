package com.ssafy.searchserver.search.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ForeignNewsListResponse {
    private String code;
    private boolean success;
    private String message;
    private List<ForeignNewsResponse> data;
}
