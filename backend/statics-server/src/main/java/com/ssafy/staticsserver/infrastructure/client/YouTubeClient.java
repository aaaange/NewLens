package com.ssafy.staticsserver.infrastructure.client;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.ssafy.staticsserver.interfaces.country.dto.VideoResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class YouTubeClient {

    private final GoogleClient googleClient;
    @Value("${YOUTUBE_API_KEY}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<VideoResponse> searchVideos(String keyword, String keywordMind, String regionCode) {
        List<VideoResponse> results;
        try {

            if(regionCode.equals("한국")) regionCode = "KR";

            String languageCode = getLanguageCode(regionCode);
            String translatedKeyword = googleClient.translateYoutube(keyword, languageCode);
            String translatedKeywordMind = keywordMind != null ? googleClient.translateYoutube(keywordMind, languageCode) : null;


            String query = translatedKeyword;
            if (translatedKeywordMind != null && !translatedKeywordMind.isBlank()) {
                query += " " + translatedKeywordMind;
            }

            String url = UriComponentsBuilder.fromHttpUrl("https://www.googleapis.com/youtube/v3/search")
                    .queryParam("key", apiKey)
                    .queryParam("part", "snippet")
                    .queryParam("q", query)
                    .queryParam("regionCode", regionCode)
                    .queryParam("relevanceLanguage", languageCode)
                    .queryParam("type", "video")
                    .queryParam("order", "relevance")
                    .queryParam("safeSearch", "moderate")
                    .queryParam("maxResults", 5)
                    .toUriString();

            ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
            JsonNode items = response.getBody().get("items");

            results = new ArrayList<>();

            if (items != null && items.isArray()) {
                for (JsonNode item : items) {
                    String title = item.get("snippet").get("title").asText("");
                    String description = item.get("snippet").get("description").asText("");

                    boolean containsKeyword = title.contains(keyword) || description.contains(keyword);
                    boolean containsKeywordMind = keywordMind != null && !keywordMind.isBlank()
                            && (title.contains(keywordMind) || description.contains(keywordMind));

//                    if (!containsKeyword && !containsKeywordMind) {
//                        continue;
//                    }

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
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                System.err.println("YouTube API regionCode 오류: " + regionCode);
            } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                System.err.println("YouTube API 제한 또는 인증 오류 (403): " + e.getMessage());
            } else {
                System.err.println("YouTube API 호출 실패: " + e.getStatusCode() + " - " + e.getMessage());
            }

            return Collections.emptyList();

        } catch (Exception e) {
            System.err.println("YouTube 예외 발생: " + e.getMessage());
            return Collections.emptyList();
        }

        return results;

    }

    private String getLanguageCode(String countryCode) {
        return switch (countryCode.toUpperCase()) {
            case "AR" -> "es";
            case "AU" -> "en";
            case "BR" -> "pt";
            case "CA" -> "en";
            case "CN" -> "zh";
            case "FR" -> "fr";
            case "DE" -> "de";
            case "IN" -> "hi";
            case "ID" -> "id";
            case "IT" -> "it";
            case "JP" -> "ja";
            case "MX" -> "es";
            case "RU" -> "ru";
            case "SA" -> "ar";
            case "ZA" -> "en";
            case "KR" -> "ko";
            case "TR" -> "tr";
            case "GB" -> "en";
            case "US" -> "en";
            default -> "en";
        };
    }
}
