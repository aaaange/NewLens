package com.ssafy.searchserver.news.service;

import com.ssafy.searchserver.news.dto.ForeignNewsListResponse;
import com.ssafy.searchserver.news.entity.ForeignNewsElastic;
import com.ssafy.searchserver.news.entity.ForeignNewsMongo;

public interface ForeignNewsService {
    //mongo
    ForeignNewsMongo save(ForeignNewsMongo news);
    ForeignNewsListResponse getNewsList();


    // elasticsearch
    // ForeignNewsElastic save(ForeignNewsElastic news);
    // ForeignNewsListResponse searchByKeyword(String keyword);
    // ForeignNewsListResponse searchByCategory(String category);

}
