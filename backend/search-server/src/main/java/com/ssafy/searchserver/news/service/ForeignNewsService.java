package com.ssafy.searchserver.news.service;

import com.ssafy.searchserver.news.dto.ForeignNewsListResponse;
import com.ssafy.searchserver.news.dto.ForeignNewsResponse;
import com.ssafy.searchserver.news.entity.ForeignNewsElastic;
import com.ssafy.searchserver.news.entity.ForeignNewsMongo;

public interface ForeignNewsService {
    //mongo
    ForeignNewsMongo save(ForeignNewsMongo news);
    ForeignNewsListResponse getNewsList();


    // elasticsearch
    ForeignNewsResponse save(ForeignNewsElastic news);
    ForeignNewsListResponse searchByKeywordCategoryAndPeriod(String keyword, String category, int period);


}
