package com.ssafy.staticsserver.application;

import com.ssafy.staticsserver.infrastructure.client.YouTubeClient;
import com.ssafy.staticsserver.interfaces.country.dto.VideoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class YoutubeTestController {
    private final YouTubeClient youTubeClient;


    @GetMapping("/test")
    public List<VideoResponse> test(
            @RequestParam String country,
            @RequestParam String keyword,
            @RequestParam String keywordMind
           ) {
       return youTubeClient.searchVideos(keyword, keywordMind, country);
    }

}
