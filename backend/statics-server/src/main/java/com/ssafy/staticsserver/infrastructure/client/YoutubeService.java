package com.ssafy.staticsserver.infrastructure.client;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.ssafy.staticsserver.interfaces.country.dto.VideoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class YoutubeService {

    private final GoogleClient googleClient;

    @Value("${YOUTUBE_API_KEY}")
    private String apiKey;

    public List<VideoResponse> searchVideos(String keyword, String keywordMind, String country) {
        try {
            if (country.equals("한국")) {
                country = "KR";
            }

            String languageCode = getLanguageCode(country);

            // 번역
            String translatedKeyword = googleClient.translateYoutube(keyword, languageCode);
            String translatedKeywordMind = keywordMind != null && !keywordMind.isBlank()
                    ? googleClient.translateYoutube(keywordMind, languageCode)
                    : null;

            String query = translatedKeyword;
            if (translatedKeywordMind != null && !translatedKeywordMind.isBlank()) {
                query += " " + translatedKeywordMind;
            }

            JsonFactory jsonFactory = new JacksonFactory();
            YouTube youtube = new YouTube.Builder(
                    new com.google.api.client.http.javanet.NetHttpTransport(),
                    jsonFactory,
                    request -> {}
            ).setApplicationName("youtube-search-service").build();

            YouTube.Search.List search = youtube.search().list(Collections.singletonList("id,snippet"));
            search.setKey(apiKey);
            search.setQ(query);
            search.setRegionCode(country);
            search.setRelevanceLanguage(languageCode);
            search.setType(Collections.singletonList("video"));
            search.setMaxResults(5L);
            search.setOrder("relevance");

            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();

            List<VideoResponse> results = new ArrayList<>();

            if (searchResultList != null) {
                for (SearchResult item : searchResultList) {
                    String videoId = item.getId().getVideoId();
                    String videoUrl = "https://www.youtube.com/watch?v=" + videoId;
                    String title = item.getSnippet().getTitle();
                    String thumbnailUrl = item.getSnippet().getThumbnails().getHigh().getUrl();

                    results.add(VideoResponse.builder()
                            .url(videoUrl)
                            .title(title)
                            .thumbnailUrl(thumbnailUrl)
                            .build());
                }
            }

            return results;

        } catch (IOException e) {
            System.err.println("YouTube API 호출 중 IOException 발생: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("YouTube 검색 처리 중 예외 발생: " + e.getMessage());
        }

        return Collections.emptyList();
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
