package com.ssafy.staticsserver.news.service;

import java.util.List;

import com.ssafy.staticsserver.news.dto.ForeignNewsListResponse;
import com.ssafy.staticsserver.news.entitiy.ForeignNewsMongo;

public interface ForeignNewsService {
	void listen(String message);
	List<ForeignNewsMongo> getNewsList(List<String> newsIds);
	void processNews(List<ForeignNewsMongo> newsDocs);
}
