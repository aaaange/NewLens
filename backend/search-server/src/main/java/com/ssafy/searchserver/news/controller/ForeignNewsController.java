package com.ssafy.searchserver.news.controller;

import com.ssafy.searchserver.common.CommonResponse;
import com.ssafy.searchserver.news.dto.ForeignNewsListResponse;
import com.ssafy.searchserver.news.service.ForeignNewsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ForeignNewsController {

    private final ForeignNewsServiceImpl foreignNewsService;


    @PostMapping("/insert_news")
    public ResponseEntity<CommonResponse> insertSampleData() {
        foreignNewsService.insertSampleNews();

        CommonResponse response = CommonResponse.builder()
                .code("SUCCESS")
                .success(true)
                .message("더미 데이터 추가 성공")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/news")
    public ResponseEntity<ForeignNewsListResponse> getNewsList() {
        ForeignNewsListResponse response = foreignNewsService.getNewsList();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

}
