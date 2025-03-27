package com.ssafy.staticsserver.common.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.ssafy.staticsserver.interfaces.country.dto.VideoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class YouTubeClient {

    @Value("${YOUTUBE_API_KEY}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<VideoResponse> searchVideos(String keyword, String keyword2, String regionCode) {
        String query = keyword;
        if (keyword2 != null && !keyword2.isBlank()) {
            query += " " + keyword2;
        }

        String url = UriComponentsBuilder.fromHttpUrl("https://www.googleapis.com/youtube/v3/search")
                .queryParam("key", apiKey)
                .queryParam("part", "snippet")
                .queryParam("q", query)
                .queryParam("regionCode", regionCode)
                .queryParam("type", "video")
                .queryParam("maxResults", 5)
                .toUriString();

        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
        JsonNode items = response.getBody().get("items");

        List<VideoResponse> results = new ArrayList<>();
        if (items != null && items.isArray()) {
            for (JsonNode item : items) {
                String videoId = item.get("id").get("videoId").asText();
                String videoUrl = "https://www.youtube.com/watch?v=" + videoId;
                String title = item.get("snippet").get("title").asText();
                String thumbnail = item.get("snippet").get("thumbnails").get("default").get("url").asText();
                String publishedAtRaw = item.get("snippet").get("publishedAt").asText();
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(publishedAtRaw);
                LocalDateTime publishedAt = zonedDateTime.toLocalDateTime();


                results.add(VideoResponse.builder()
                        .url(videoUrl)
                        .title(title)
                        .thumbnailUrl(thumbnail)
                        .publishedAt(publishedAt)
                        .build());
            }
        }

        return results;
    }
}
