package com.ssafy.searchserver.interfaces.country.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import com.ssafy.searchserver.application.country.CountryService;

@RestController
@RequiredArgsConstructor
public class CountryCallbackController {
	private final CountryService countryService;

	@PostMapping("/api/search/country/compare-callback")
	public ResponseEntity<Void> receiveGptCompareSummary(
		@RequestParam("requestId") String requestId,
		@RequestBody String summary
	) {
		CompletableFuture<String> future = countryService.removeFuture(requestId);
		if (future != null) {
			future.complete(summary);
		}
		System.out.println(summary);
		return ResponseEntity.ok().build();
	}

}
