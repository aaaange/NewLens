package com.ssafy.staticsserver.infrastructure.client;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.ssafy.staticsserver.interfaces.country.dto.VideoResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class YouTubeClient {

    @Value("${YOUTUBE_API_KEY}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<VideoResponse> searchVideos(String keyword, String keywordMind, String regionCode) {

        String query = keyword;
        if (keywordMind != null && !keywordMind.isBlank()) {
            query += " " + keywordMind;
        }

        String url = UriComponentsBuilder.fromHttpUrl("https://www.googleapis.com/youtube/v3/search")
                .queryParam("key", apiKey)
                .queryParam("part", "snippet")
                .queryParam("q", query)
                .queryParam("regionCode", regionCode)
//                .queryParam("relevanceLanguage", getLanguageCode(regionCode))
                .queryParam("type", "video")
                .queryParam("order", "relevance")
                .queryParam("safeSearch", "moderate")
                .queryParam("maxResults", 10)
                .toUriString();

        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
        JsonNode items = response.getBody().get("items");
        System.out.println(items.toString());

        List<VideoResponse> results = new ArrayList<>();

        if (items != null && items.isArray()) {
            for (JsonNode item : items) {
                String title = item.get("snippet").get("title").asText("");
                String description = item.get("snippet").get("description").asText("");

                boolean containsKeyword = title.contains(keyword) || description.contains(keyword);
                boolean containsKeywordMind = keywordMind != null && !keywordMind.isBlank()
                        && (title.contains(keywordMind) || description.contains(keywordMind));

                if (!containsKeyword && !containsKeywordMind) {
                    continue;
                }

                String videoId = item.get("id").get("videoId").asText();
                String videoUrl = "https://www.youtube.com/watch?v=" + videoId;
                String thumbnail = item.get("snippet").get("thumbnails").get("high").get("url").asText();
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

    private String getLanguageCode(String countryCode) {
        return switch (countryCode.toUpperCase()) {
            case "KR" -> "ko";
            case "RU" -> "ru";
            case "US" -> "en";
            case "JP" -> "ja";
            case "CN" -> "zh";
            case "FR" -> "fr";
            default -> "en";
        };
    }
}
