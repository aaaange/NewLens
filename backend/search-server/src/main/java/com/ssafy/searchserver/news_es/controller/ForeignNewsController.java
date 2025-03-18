package com.ssafy.searchserver.news_es.controller;

import com.ssafy.searchserver.news_es.dto.ForeignNewsListResponse;
import com.ssafy.searchserver.news_es.entity.ForeignNews;
import com.ssafy.searchserver.news_es.service.ForeignNewsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class ForeignNewsController {

    private final ForeignNewsServiceImpl service;

    @PostMapping
    public ForeignNews saveNews(@RequestBody ForeignNews news) {
        return service.save(news);
    }


    @GetMapping("/keyword")
    public ResponseEntity<ForeignNewsListResponse> getNewsByKeyword(@RequestParam String keyword) {
        ForeignNewsListResponse response = service.searchByKeyword(keyword);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/category")
    public ResponseEntity<ForeignNewsListResponse> getNewsByCategory(@RequestParam String category) {
        ForeignNewsListResponse response = service.searchByCategory(category);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
