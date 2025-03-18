package com.ssafy.searchserver.news.service;

import com.ssafy.searchserver.news.dto.ForeignNewsListResponse;
import com.ssafy.searchserver.news.dto.ForeignNewsResponse;
import com.ssafy.searchserver.news.entity.ForeignNews;
import com.ssafy.searchserver.news.repository.ForeignNewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ForeignNewsServiceImpl implements ForeignNewsService {

    private final ForeignNewsRepository foreignNewsRepository;

    public void insertSampleNews() {
        ForeignNews news = ForeignNews.builder()
                .title("세계 경제 동향 보고서")
                .description("세계 경제가 회복세를 보이고 있습니다.")
                .url("https://example.com/world-economy")
                .publishedAt(LocalDateTime.of(2024, 12, 1, 10, 30))
                .imageUrl("https://example.com/image.jpg")
                .categories(Arrays.asList("경제", "국제"))
                .country("US")
                .keywords(Arrays.asList("경제", "회복", "세계"))
                .sentiment(80)
                .rawDataRef("ref_12345")
                .build();

        foreignNewsRepository.save(news);
    }

    @Override
    public ForeignNewsListResponse getNewsList() {
        List<ForeignNewsResponse> newsList = foreignNewsRepository.findAll().stream()
                .map(news -> ForeignNewsResponse.builder()
                        .id(news.getId())
                        .title(news.getTitle())
                        .description(news.getDescription())
                        .publishedAt(news.getPublishedAt())
                        .imageUrl(news.getImageUrl())
                        .url(news.getUrl())
                        .build())
                .collect(Collectors.toList());

        return ForeignNewsListResponse.builder()
                .code("SUCCESS")
                .success(true)
                .message("요청 성공")
                .data(newsList)
                .build();
    }


}
