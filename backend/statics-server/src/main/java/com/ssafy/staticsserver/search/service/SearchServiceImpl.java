package com.ssafy.staticsserver.search.service;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.staticsserver.search.entitiy.ForeignNewsMongo;
import com.ssafy.staticsserver.search.repository.ForeignNewsMongoDBRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchNewsService {

	private final ObjectMapper objectMapper;
	private final ForeignNewsMongoDBRepository mongoDBRepository;

	@KafkaListener(topics = "related-keywords")
	public void listenRelatedKeywords(String message) {
		try {
			List<String> newsIds = objectMapper.readValue(message, new TypeReference<List<String>>() {
			});
			List<ForeignNewsMongo> newsList = mongoDBRepository.findByIdIn(newsIds);
			System.out.println("연관어 처리를 위한 뉴스 리스트");
			for (ForeignNewsMongo foreignNewsMongo : newsList) {
				System.out.println(foreignNewsMongo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@KafkaListener(topics = "keyword-ranking")
	public void listenKeywordRanking(String message) {
		try {
			List<String> newsIds = objectMapper.readValue(message, new TypeReference<List<String>>() {
			});
			List<ForeignNewsMongo> newsList = mongoDBRepository.findByIdIn(newsIds);
			System.out.println("키워드 처리를 위한 뉴스 리스트");
			for (ForeignNewsMongo foreignNewsMongo : newsList) {
				System.out.println(foreignNewsMongo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@KafkaListener(topics = "worldwide")
	public void listenWorldwide(String message) {
		try {
			List<String> newsIds = objectMapper.readValue(message, new TypeReference<List<String>>() {
			});
			List<ForeignNewsMongo> newsList = mongoDBRepository.findByIdIn(newsIds);
			System.out.println("세계 지도 처리를 위한 뉴스 리스트");
			for (ForeignNewsMongo foreignNewsMongo : newsList) {
				System.out.println(foreignNewsMongo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 관련 키워드 집계 로직
	@Override
	public void processRelatedKeywords(List<ForeignNewsMongo> newsList) {
	}

	// 키워드 랭킹 집계 로직
	@Override
	public void processKeywordRanking(List<ForeignNewsMongo> newsList) {
	}

	// 세계지도 집계 로직
	@Override
	public void processWorldwide(List<ForeignNewsMongo> newsList) {
	}
}