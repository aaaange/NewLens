package com.ssafy.searchserver.news_es.service;

import com.ssafy.searchserver.news_es.dto.ForeignNewsListResponse;
import com.ssafy.searchserver.news_es.dto.ForeignNewsResponse;
import com.ssafy.searchserver.news_es.entity.ForeignNews;
import com.ssafy.searchserver.news_es.repository.ForeignNewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ForeignNewsServiceImpl implements ForeignNewsService {

    private final ForeignNewsRepository foreignNewsRepository;

    public ForeignNews save(ForeignNews news) {
        return foreignNewsRepository.save(news);
    }


    public ForeignNewsListResponse searchByKeyword(String keyword) {
        List<ForeignNewsResponse> newsList = foreignNewsRepository.findByKeywordsContaining(keyword).stream()
                .map(news -> ForeignNewsResponse.builder()
                        .id(news.getId())
                        .title(news.getTitle())
                        .description(news.getDescription())
                        .publishedAt(news.getPublished_at())
                        .imageUrl(news.getImage_url())
                        .url(news.getUrl())
                        .country(news.getCountry())
                        .categories(news.getCategories())
                        .keywords(news.getKeywords())
                        .build())
                .collect(Collectors.toList());

        return ForeignNewsListResponse.builder()
                .code("SUCCESS")
                .success(true)
                .message("요청 성공")
                .data(newsList)
                .build();
    }

    public ForeignNewsListResponse searchByCategory(String category) {
        List<ForeignNewsResponse> newsList = foreignNewsRepository.findByCategoriesContaining(category).stream()
                .map(news -> ForeignNewsResponse.builder()
                        .id(news.getId())
                        .title(news.getTitle())
                        .description(news.getDescription())
                        .publishedAt(news.getPublished_at())
                        .imageUrl(news.getImage_url())
                        .url(news.getUrl())
                        .country(news.getCountry())
                        .categories(news.getCategories())
                        .keywords(news.getKeywords())
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

