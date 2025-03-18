package com.ssafy.searchserver.news.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.searchserver.news.dto.ForeignNewsListResponse;
import com.ssafy.searchserver.news.dto.ForeignNewsResponse;
import com.ssafy.searchserver.news.entity.ForeignNewsElastic;
import com.ssafy.searchserver.news.entity.ForeignNewsMongo;
import com.ssafy.searchserver.news.service.ForeignNewsServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class ForeignNewsController {

    private final ForeignNewsServiceImpl service;

    //mongo
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

    //elasticsearch
    @PostMapping("es")
    public ForeignNewsResponse saveNews(@RequestBody ForeignNewsElastic news) {
        return service.save(news);
    }


    @GetMapping("/es/keyword")
    public ResponseEntity<ForeignNewsListResponse> getNewsByKeyword(@RequestParam String keyword) {
        ForeignNewsListResponse response = service.searchByKeyword(keyword);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/es/category")
    public ResponseEntity<ForeignNewsListResponse> getNewsByCategory(@RequestParam String category) {
        ForeignNewsListResponse response = service.searchByCategory(category);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/es/period")
    public ResponseEntity<ForeignNewsListResponse> getNewsByPeriod(@RequestParam int period) {
        ForeignNewsListResponse response = service.searchByPeriod(period);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
