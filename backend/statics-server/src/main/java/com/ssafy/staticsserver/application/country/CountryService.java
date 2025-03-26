package com.ssafy.staticsserver.application.country;

import java.util.List;
import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.staticsserver.domain.news.model.ForeignNewsMongo;
import com.ssafy.staticsserver.domain.news.repository.ForeignNewsMongoDBRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CountryService {
	private final ObjectMapper objectMapper;
	private final ForeignNewsMongoDBRepository mongoDBRepository;

	@KafkaListener(topics = "dashboard")
	public void listenDashboard(String message) {
		try {
			List<String> newsIds = objectMapper.readValue(message, new TypeReference<List<String>>() {
			});
			List<ForeignNewsMongo> newsList = mongoDBRepository.findByIdIn(newsIds);
			System.out.println("국가별 대시보드 처리를 위한 뉴스 리스트");
			for (ForeignNewsMongo foreignNewsMongo : newsList) {
				System.out.println(foreignNewsMongo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@KafkaListener(topics = "compare-into")
	public void listenCompareInto(String message) {

	}

	@KafkaListener(topics = "news-modal")
	public void listenNews(String message) {

	}

	// 국가별 대시보드 집계 로직
	public void processDashboard(List<ForeignNewsMongo> newsList) {
	}

	// 국가별 비교 세문장 GPT 집계 로직
	public void processCompareInto(List<ForeignNewsMongo> newsList) {
	}

	// 관련 키워드 집계 로직
	public void processNews(List<ForeignNewsMongo> newsList) {
	}

}
