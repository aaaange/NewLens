package com.ssafy.searchserver.search.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.searchserver.search.dto.ForeignNewsListResponse;
import com.ssafy.searchserver.search.dto.ForeignNewsResponse;
import com.ssafy.searchserver.search.entity.ForeignNewsElastic;
import com.ssafy.searchserver.search.entity.ForeignNewsMongo;
import com.ssafy.searchserver.search.service.SearchServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

	private final SearchServiceImpl service;

	//mongo 더미데이터
	@PostMapping("/mongo")
	public ForeignNewsMongo save(@RequestBody ForeignNewsMongo news) {
		return service.save(news);
	}

	@GetMapping("/mongo")
	public ResponseEntity<ForeignNewsListResponse> getMongoDBNewsList() {
		ForeignNewsListResponse response = service.getNewsList();
		return ResponseEntity
			.status(HttpStatus.OK)
			.body(response);
	}

	//elasticsearch 더미데이터
	@PostMapping("/es")
	public ForeignNewsResponse saveNews(@RequestBody ForeignNewsElastic news) {
		return service.save(news);
	}

	@GetMapping("/extract-related_words")
	public ResponseEntity<ForeignNewsListResponse> getRelatedWords(@RequestParam String keyword, String category,
		int period) {
		ForeignNewsListResponse response = service.getRelatedWords(keyword, category, period);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/keyword-ranking")
	public ResponseEntity<ForeignNewsListResponse> getKeywordRanking(@RequestParam String category,
		int period, boolean is_korea) {
		ForeignNewsListResponse response = service.getKeywordRanking(category, period, is_korea);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/worldwide")
	public ResponseEntity<ForeignNewsListResponse> getWorldwide(@RequestParam String keyword, String category,
		int period) {
		ForeignNewsListResponse response = service.getWorldwide(keyword, category, period);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

}
