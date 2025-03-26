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

@RestController
@RequiredArgsConstructor
public class CountryCallbackController {

    private final Map<String, CompletableFuture<String>> pendingCompareResults = new ConcurrentHashMap<>();

    @PostMapping("/api/search/country/compare-callback")
    public ResponseEntity<Void> receiveGptCompareSummary(
            @RequestParam("requestId") String requestId,
            @RequestBody String summary
    ) {
        CompletableFuture<String> future = pendingCompareResults.remove(requestId);
        if (future != null) {
            future.complete(summary);
        }
        return ResponseEntity.ok().build();
    }



}
