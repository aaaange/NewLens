package com.ssafy.staticsserver.search.service;

import java.util.List;

import com.ssafy.staticsserver.search.entitiy.ForeignNewsMongo;

public interface SearchNewsService {
	void listenRelatedKeywords(String message);
	void listenKeywordRanking(String message);
	void listenWorldwide(String message);
	void processRelatedKeywords(List<ForeignNewsMongo> newsList);
	void processKeywordRanking(List<ForeignNewsMongo> newsList);
	void processWorldwide(List<ForeignNewsMongo> newsList);
}
