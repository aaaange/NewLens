package com.ssafy.searchserver.news.service;

import com.ssafy.searchserver.news.dto.ForeignNewsListResponse;
import org.springframework.http.ResponseEntity;

public interface ForeignNewsService {
    void insertSampleNews();
    ForeignNewsListResponse getNewsList();
}
