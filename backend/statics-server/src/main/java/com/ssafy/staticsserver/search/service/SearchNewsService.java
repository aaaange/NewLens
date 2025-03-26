package com.ssafy.staticsserver.search.service;

import java.util.List;
import java.util.Map;

import com.ssafy.staticsserver.search.dto.SentimentMentionResponse;
import com.ssafy.staticsserver.search.entitiy.ForeignNewsMongo;

public interface SearchNewsService {
	void listenRelatedKeywords(String message);
	void listenKeywordRanking(String message);
	void listenWorldwide(String message);
	void processRelatedKeywords(List<ForeignNewsMongo> newsList);
	Map<String, Object> processKeywordRanking(List<ForeignNewsMongo> newsList);
	SentimentMentionResponse processWorldwide(List<ForeignNewsMongo> newsList);
}
