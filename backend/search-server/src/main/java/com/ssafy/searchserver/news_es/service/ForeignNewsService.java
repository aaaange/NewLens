package com.ssafy.searchserver.news_es.service;

import com.ssafy.searchserver.news_es.dto.ForeignNewsListResponse;
import com.ssafy.searchserver.news_es.dto.ForeignNewsResponse;
import com.ssafy.searchserver.news_es.entity.ForeignNews;
import com.ssafy.searchserver.news_es.repository.ForeignNewsRepository;

import java.util.List;

public interface ForeignNewsService {
    ForeignNews save(ForeignNews news);

    ForeignNewsListResponse searchByKeyword(String keyword);
    ForeignNewsListResponse searchByCategory(String category);
}
