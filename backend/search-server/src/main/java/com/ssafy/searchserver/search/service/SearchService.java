package com.ssafy.searchserver.search.service;

import com.ssafy.searchserver.search.dto.ForeignNewsListResponse;
import com.ssafy.searchserver.search.dto.ForeignNewsResponse;
import com.ssafy.searchserver.search.entity.ForeignNewsElastic;
import com.ssafy.searchserver.search.entity.ForeignNewsMongo;

public interface SearchService {
    //mongo
    ForeignNewsMongo save(ForeignNewsMongo news);
    ForeignNewsListResponse getNewsList();


    // elasticsearch
    ForeignNewsResponse save(ForeignNewsElastic news);
    ForeignNewsListResponse getRelatedWords(String keyword, String category, int period);
    ForeignNewsListResponse getKeywordRanking(String category, int period, boolean is_korea);
    ForeignNewsListResponse getWorldwide(String keyword, String category, int period);


}
