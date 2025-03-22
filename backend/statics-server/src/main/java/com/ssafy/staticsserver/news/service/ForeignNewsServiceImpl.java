package com.ssafy.staticsserver.news.service;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.staticsserver.news.entitiy.ForeignNewsMongo;
import com.ssafy.staticsserver.news.repository.ForeignNewsMongoDBRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ForeignNewsServiceImpl implements ForeignNewsService {

	private final ObjectMapper objectMapper;
	private final ForeignNewsMongoDBRepository mongoDBRepository;

	@KafkaListener(topics = "foreign_news")
	public void listen(String message) {
		try {
			// Kafka 메시지 수신
			List<String> newsIds = objectMapper.readValue(message, new TypeReference<List<String>>() {
			});
			System.out.println("Kafka 메시지 수신(뉴스 ID 리스트): " + newsIds);

			// MongoDB 조회
			List<ForeignNewsMongo> newsList = getNewsList(newsIds);
			for (ForeignNewsMongo newsDoc : newsList) {
				System.out.println(newsDoc.toString());
			}

			// 실제 통계/로직 처리
			processNews(newsList);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<ForeignNewsMongo> getNewsList(List<String> newsIds) {
		List<ForeignNewsMongo> newsList = mongoDBRepository.findByIdIn(newsIds);
		return newsList;
	}

	public void processNews(List<ForeignNewsMongo> newsList) {

	}
}
