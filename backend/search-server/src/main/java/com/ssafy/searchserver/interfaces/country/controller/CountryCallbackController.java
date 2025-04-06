package com.ssafy.searchserver.interfaces.country.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import com.ssafy.searchserver.application.country.CountryService;
import com.ssafy.searchserver.interfaces.country.dto.AnalysisData;
import com.ssafy.searchserver.interfaces.country.dto.DashboardData;
import com.ssafy.searchserver.interfaces.country.dto.NewsModalResponse;

@RestController
@RequestMapping("/api/search/country")
@RequiredArgsConstructor
public class CountryCallbackController {
    private final CountryService countryService;

    @PostMapping("/dashboard_callback")
    public ResponseEntity<Void> receiveDashboard(
            @RequestParam("requestId") String requestId,
            @RequestBody DashboardData response
    ) {
        CompletableFuture<?> future = countryService.removeFuture(requestId);
        if (future != null) {
            CompletableFuture<DashboardData> newFuture = (CompletableFuture<DashboardData>) future;
            newFuture.complete(response);
        }
        System.out.println(response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/dashboard_gpt_callback")
    public ResponseEntity<Void> receiveDashboardGpt(
            @RequestParam("requestId") String requestId,
            @RequestBody String summary
    ) {
        CompletableFuture<String> future = (CompletableFuture<String>) countryService.removeFuture(requestId);
        if (future != null) {
            future.complete(summary);
        }
        return ResponseEntity.ok().build();
    }


    @PostMapping("/compare_callback")
    public ResponseEntity<Void> receiveGptCompareSummary(
            @RequestParam("requestId") String requestId,
            @RequestBody String summary
    ) {
        CompletableFuture<?> future = countryService.removeFuture(requestId);
        if (future != null) {
            CompletableFuture<String> stringFuture = (CompletableFuture<String>) future;
            stringFuture.complete(summary);
        }
        System.out.println(summary);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/news_modal_callback")
    public ResponseEntity<Void> receiveNewsModal(
            @RequestParam("requestId") String requestId,
            @RequestBody NewsModalResponse response
    ) {
        CompletableFuture<?> future = countryService.removeFuture(requestId);
        if (future != null) {
            CompletableFuture<NewsModalResponse> newFuture = (CompletableFuture<NewsModalResponse>) future;
            newFuture.complete(response);
        }
        return ResponseEntity.ok().build();
    }

}
