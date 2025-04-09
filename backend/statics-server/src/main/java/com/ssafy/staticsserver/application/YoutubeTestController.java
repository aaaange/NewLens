package com.ssafy.staticsserver.application;

import com.ssafy.staticsserver.infrastructure.client.YouTubeClient;
import com.ssafy.staticsserver.infrastructure.client.YoutubeService;
import com.ssafy.staticsserver.interfaces.country.dto.VideoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class YoutubeTestController {
    private final YouTubeClient youTubeClient;
    private final YoutubeService youtubeService;

    @GetMapping("/youtube")
    public ResponseEntity<List<VideoResponse>> searchVideo(@RequestParam String keyword,
                                                           @RequestParam String keywordMind
    ,@RequestParam String country) throws IOException {
        // YoutubeService를 통해 동영상 검색한 결과를 받아옴
       List<VideoResponse> result = youtubeService.searchVideos(keyword, keywordMind, country);
        return ResponseEntity.ok(result);

    }


    @GetMapping("/test")
    public List<VideoResponse> test(
            @RequestParam String country,
            @RequestParam String keyword,
            @RequestParam String keywordMind
           ) {
       return youTubeClient.searchVideos(keyword, keywordMind, country);
    }

}
