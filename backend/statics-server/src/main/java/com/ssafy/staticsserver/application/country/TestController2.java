package com.ssafy.staticsserver.application.country;

import com.ssafy.staticsserver.infrastructure.client.YouTubeClient;
import com.ssafy.staticsserver.interfaces.country.dto.VideoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TestController2 {
    private final YouTubeClient youTubeClient;

    @GetMapping("/test2")
    public List<VideoResponse> test(
            @RequestParam String country,
            @RequestParam String keyword,
            @RequestParam String keywordMind
    ) {
        return youTubeClient.searchVideos(keyword, keywordMind, country);
    }


}
