package com.ssafy.searchserver.interfaces.search.controller;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.searchserver.application.search.SearchService;
import com.ssafy.searchserver.interfaces.search.dto.KeywordRankingData;
import com.ssafy.searchserver.interfaces.search.dto.SentimentMentionData;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchCallbackController {
	private final SearchService searchService;

	@PostMapping("/worldwide_callback")
	public ResponseEntity<Void> receiveWorldwide(
		@RequestParam("requestId") String requestId,
		@RequestBody SentimentMentionData response
	){
		CompletableFuture<?> future = searchService.removeFuture(requestId);
		if(future!=null){
			CompletableFuture<SentimentMentionData> newFuture = (CompletableFuture<SentimentMentionData>) future;
			newFuture.complete(response);
		}
		return ResponseEntity.ok().build();
	}

	@PostMapping("/keyword_ranking_callback")
	public ResponseEntity<Void> receiveKeywordRanking(
		@RequestParam("requestId") String requestId,
		@RequestBody KeywordRankingData response
	){
		CompletableFuture<?> future = searchService.removeFuture(requestId);
		if(future!=null){
			CompletableFuture<KeywordRankingData> newFuture = (CompletableFuture<KeywordRankingData>) future;
			newFuture.complete(response);
		}
		return ResponseEntity.ok().build();
	}
}
