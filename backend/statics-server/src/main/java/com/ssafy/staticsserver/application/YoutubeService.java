package com.ssafy.staticsserver.application;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.ssafy.staticsserver.interfaces.country.dto.VideoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class YoutubeService {

    @Value("${YOUTUBE_API_KEY}")
    private String apiKey;

    public List<VideoResponse> searchVideos(String query) throws IOException {
        JsonFactory jsonFactory = new JacksonFactory();

        YouTube youtube = new YouTube.Builder(
                new com.google.api.client.http.javanet.NetHttpTransport(),
                jsonFactory,
                request -> {})
                .setApplicationName("youtube-search-service")
                .build();

        YouTube.Search.List search = youtube.search().list(Collections.singletonList("id,snippet"));
        search.setKey(apiKey);
        search.setQ(query);
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

                VideoResponse response = VideoResponse.builder()
                        .url(videoUrl)
                        .title(title)
                        .thumbnailUrl(thumbnailUrl)
                        .build();

                results.add(response);
            }
        }

        return results;
    }
}
