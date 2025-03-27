package com.ssafy.staticsserver.application.country;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ssafy.staticsserver.common.config.GptClient;
import com.ssafy.staticsserver.interfaces.country.dto.CountryNewsMessage;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.staticsserver.domain.news.model.ForeignNewsMongo;
import com.ssafy.staticsserver.domain.news.repository.ForeignNewsMongoDBRepository;
import com.ssafy.staticsserver.interfaces.country.dto.NewsDto;
import com.ssafy.staticsserver.interfaces.country.dto.NewsModalResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CountryService {
    private final ObjectMapper objectMapper;
    private final ForeignNewsMongoDBRepository mongoDBRepository;
    private final GptClient gptClient;

    @KafkaListener(topics = "dashboard")
    public void listenDashboard(String message) {
        try {
            List<String> newsIds = objectMapper.readValue(message, new TypeReference<List<String>>() {
            });
            List<ForeignNewsMongo> newsList = mongoDBRepository.findByIdIn(newsIds);
            System.out.println("국가별 대시보드 처리를 위한 뉴스 리스트");
            for (ForeignNewsMongo foreignNewsMongo : newsList) {
                System.out.println(foreignNewsMongo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "compare-info")
    public void listenCompareInfo(String message) {
        try {
            CountryNewsMessage msg = objectMapper.readValue(message, new TypeReference<>() {
            });
            String keyword = msg.getKeyword();
            String keywordMind = msg.getKeywordMind();
            String country1 = msg.getCountry1();
            String country2 = msg.getCountry2();
            List<ForeignNewsMongo> newsList1 = mongoDBRepository.findByIdIn(msg.getCountry1NewsIds());
            List<ForeignNewsMongo> newsList2 = mongoDBRepository.findByIdIn(msg.getCountry2NewsIds());
            newsList1.sort((a, b) -> b.getPublishedAt().compareTo(a.getPublishedAt()));
            newsList2.sort((a, b) -> b.getPublishedAt().compareTo(a.getPublishedAt()));


            String prompt = buildComparePrompt(
                    keyword, keywordMind,
                    country1, newsList1,
                    country2, newsList2
            );
            String comparisonSummary = gptClient.ask(prompt);
            System.out.println(comparisonSummary);
//            String result = "결과";



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "news-modal")
    public void listenNews(String message) {
        try {
            Map<String, Object> payload = objectMapper.readValue(message, new TypeReference<>() {});

            List<String> newsIds = (List<String>) payload.get("newsIds");
            int page = (Integer) payload.get("page");
            int size = (Integer) payload.get("size");

            List<ForeignNewsMongo> newsList = mongoDBRepository.findByIdIn(newsIds);
            System.out.println("뉴스 모달창을 위한 뉴스 리스트");
            for (ForeignNewsMongo foreignNewsMongo : newsList) {
                System.out.println(foreignNewsMongo);
            }

            NewsModalResponse response = processNews(newsList, page, size);
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 국가별 대시보드 집계 로직
    public void processDashboard(List<ForeignNewsMongo> newsList) {
    }

    //  GPT 한줄 요약
    public String buildComparePrompt(
            String keyword, String keywordMind,
            String country1, List<ForeignNewsMongo> news1,
            String country2, List<ForeignNewsMongo> news2
    ) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("[국가 비교 뉴스 여론 분석 요청]\n\n");
        prompt.append("다음은 \"").append(keyword);
        if (keywordMind != null && !keywordMind.isBlank()) {
            prompt.append(" (").append(keywordMind).append(")");
        }
        prompt.append("\" 키워드와 관련된 ").append(country1).append("과 ").append(country2).append("의 뉴스입니다.\n\n");

        prompt.append("[").append(country1).append(" 뉴스]").append("\n");
        for (int i = 0; i < news1.size(); i++) {
            ForeignNewsMongo news = news1.get(i);
            prompt.append(i + 1).append(". ").append(news.getTitle()).append("\n");
            prompt.append("- ").append(news.getDescription()).append("\n\n");
        }

        prompt.append("[").append(country2).append(" 뉴스]").append("\n");
        for (int i = 0; i < news2.size(); i++) {
            ForeignNewsMongo news = news2.get(i);
            prompt.append(i + 1).append(". ").append(news.getTitle()).append("\n");
            prompt.append("- ").append(news.getDescription()).append("\n\n");
        }

        prompt.append("위 뉴스를 참고하여, ").append(country1).append("과 ").append(country2)
                .append("이 ").append(keyword).append("에 대해 어떤 시각/전략/관점을 가지고 있는지 한 문장으로 비교 요약해 주세요. 한국어로 작성해 주세요.");

        return prompt.toString();
    }

    // 뉴스 리스트 모달창 출력을 위한 집계 로직
    public NewsModalResponse processNews(List<ForeignNewsMongo> newsList, int page, int size) {
        // 1. 최신순 정렬(날짜 필드 타입에 맞춰서 정렬 로직 적용)
        newsList.sort((n1, n2) -> n2.getPublishedAt().compareTo(n1.getPublishedAt()));

        int totalElements = newsList.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        // 현재 페이지 범위 계산
        // page는 1부터 시작한다고 가정
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, (int) totalElements);

        // 만약 fromIndex가 전체 크기를 벗어나면 빈 리스트 처리
        List<ForeignNewsMongo> paginatedList = Collections.emptyList();
        if (fromIndex < totalElements) {
            paginatedList = newsList.subList(fromIndex, toIndex);
        }

        // 2. DTO 변환
        List<NewsDto> newsDtos = paginatedList.stream()
                .map(item -> {
                    // sentiment를 66 이상 / 34~65 / 0~33 으로 구분하려면 여기서 처리
                    // 예: 숫자를 그대로 내려준다고 가정
                    return NewsDto.builder()
                            .title(item.getTitle())
                            .url(item.getUrl())
                            .publishedAt(item.getPublishedAt())
                            .imageUrl(item.getImageUrl())
                            .sentiment(item.getSentiment())
                            .keywords(item.getKeywords())
                            .build();
                })
                .collect(Collectors.toList());

        // 3. 페이징 정보 설정
        boolean hasNext = page < totalPages;
        boolean hasPrevious = page > 1 && totalPages > 0;

        // 4. Response DTO 빌드
        return NewsModalResponse.builder()
                .news(newsDtos)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .hasNext(hasNext)
                .hasPrevious(hasPrevious)
                .build();
    }

}
