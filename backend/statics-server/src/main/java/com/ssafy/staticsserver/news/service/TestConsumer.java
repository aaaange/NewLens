package com.ssafy.staticsserver.news.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TestConsumer {

	private final ObjectMapper objectMapper;

	@Autowired
	public TestConsumer(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@KafkaListener(topics = "foreign_news", groupId = "statics-group")
	public void listen(String message) {
		try {
			List<String> newsIds = objectMapper.readValue(message, new TypeReference<List<String>>() {});
			System.out.println("Received News IDs: " + newsIds);

			// 여기서 받은 ID 리스트로 후속 로직 처리
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}