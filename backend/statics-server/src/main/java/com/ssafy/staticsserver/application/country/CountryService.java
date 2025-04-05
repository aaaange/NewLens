package com.ssafy.staticsserver.application.country;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.ssafy.staticsserver.domain.news.repository.DomesticNewsRepositoryImpl;
import com.ssafy.staticsserver.domain.news.repository.ForeignNewsRepositoryImpl;
import com.ssafy.staticsserver.domain.news.repository.NewsMongoDBRepository;
import com.ssafy.staticsserver.infrastructure.client.GptClient;
import com.ssafy.staticsserver.infrastructure.client.KakaoClient;
import com.ssafy.staticsserver.infrastructure.client.YouTubeClient;
import com.ssafy.staticsserver.interfaces.country.dto.*;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.staticsserver.domain.news.model.ForeignNewsMongo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CountryService {
    private final ObjectMapper objectMapper;
    private final GptClient gptClient;
    private final YouTubeClient youTubeClient;
    private final ForeignNewsRepositoryImpl foreignRepo;
    private final DomesticNewsRepositoryImpl domesticRepo;
    private final KakaoClient kakaoClient;

    // 공통 인터페이스로 isKorea 로 국내, 해외 레포지토리 선택
    private NewsMongoDBRepository repository(boolean isKorea) {
        return isKorea ? domesticRepo : foreignRepo;
    }

    private static final Map<String, String> G20_COUNTRIES = Map.ofEntries(
            Map.entry("AR", "아르헨티나"), Map.entry("AU", "호주"), Map.entry("BR", "브라질"), Map.entry("CA", "캐나다"),
            Map.entry("CN", "중국"), Map.entry("FR", "프랑스"),
            Map.entry("DE", "독일"), Map.entry("IN", "인도"), Map.entry("ID", "인도네시아"), Map.entry("IT", "이탈리아"),
            Map.entry("JP", "일본"), Map.entry("MX", "멕시코"),
            Map.entry("RU", "러시아"), Map.entry("SA", "사우디아라비아"), Map.entry("ZA", "남아프리카공화국"), Map.entry("KR", "한국"),
            Map.entry("TR", "터키"), Map.entry("GB", "영국"),
            Map.entry("US", "미국"), Map.entry("EU", "유럽연합"));

    @KafkaListener(topics = "dashboard")
    public void listenDashboard(String message) {
        try {

            long start = System.currentTimeMillis();
            Map<String, Object> payload = objectMapper.readValue(message, new TypeReference<>() {
            });

            List<String> newsIds = (List<String>) payload.get("newsIds");
            int period = (Integer) payload.get("period");
            String keyword = (String) payload.get("keyword");
            String keywordMind = (String) payload.get("keyword_mind");
            boolean isKorea = (Boolean) payload.get("isKorea");
            String country = (String) payload.get("country");
            if (country.isEmpty())
                country = "한국";
            List<KeywordResponse> wordCloud = (List<KeywordResponse>) payload.get("wordCloud");
            String callbackUrl = payload.get("callbackUrl").toString();
            String requestId = payload.get("requestId").toString();

            List<ForeignNewsMongo> newsList = repository(isKorea).findByIdIn(newsIds);
            newsList.sort((a, b) -> b.getPublishedAt().compareTo(a.getPublishedAt()));

            // sentiment & mentions
            List<SentimentResponse> sentiment;
            List<MentionResponse> mentions;

            if (period == 1) {
                sentiment = processDailySentiment(newsList);
                mentions = processDailyMentions(newsList);
            } else if (period == 7) {
                sentiment = processWeeklySentiment(newsList);
                mentions = processWeeklyMentions(newsList);
            } else if (period == 30) {
                sentiment = processMonthlySentiment(newsList);
                mentions = processMonthlyMentions(newsList);
            } else {
                throw new NoSuchElementException("원하는 감정, 언급량 정보를 찾을 수 없습니다.");
            }

            // articles
            List<ArticleResponse> articles = processArticles(newsList);

            // videos
            List<VideoResponse> videos;
            if (!newsList.isEmpty()) {
                videos = processVideos(keyword, keywordMind, country);
            } else
                videos = new ArrayList<>();

            DashboardData response = DashboardData.builder()
                    .wordcloud(wordCloud)
                    .sentiment(sentiment)
                    .mentions(mentions)
                    .articles(articles)
                    .videos(videos)
                    .build();

            String responseJson = objectMapper.writeValueAsString(response);

            // 콜백 요청 전송
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(callbackUrl + "?requestId=" + requestId))
                    .POST(HttpRequest.BodyPublishers.ofString(responseJson))
                    .header("Content-Type", "application/json")
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            long end = System.currentTimeMillis();
            int newsSize = newsIds.size();
            System.out.println("뉴스 " + newsSize + "개 ====> 대시보드 통계 시간: " + (end - start) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "dashboard_gpt")
    public void listenGPT(String message) {
        Map<String, Object> payload = null;
        try {
            payload = objectMapper.readValue(message, new TypeReference<>() {
            });
            String keyword = (String) payload.get("keyword");
            String requestId = payload.get("requestId").toString();
            String callbackUrl = payload.get("callbackUrl").toString();
            String keywordMind = (String) payload.get("keyword_mind");
            String country = (String) payload.get("country");
            int period = (Integer) payload.get("period");
            List<String> newsIds = (List<String>) payload.get("newsIds");
            boolean isKorea = (Boolean) payload.get("isKorea");
            String countryName = G20_COUNTRIES.getOrDefault(country, "한국");

            List<ForeignNewsMongo> newsList = repository(isKorea).findByIdIn(newsIds);

            String description;
            if (!newsList.isEmpty()) {
                String prompt = makeDescription(keyword, keywordMind, countryName, period);
                description = gptClient.ask(prompt);
            } else
                description = "관련된 뉴스가 없습니다.";

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(callbackUrl + "?requestId=" + requestId))
                    .POST(HttpRequest.BodyPublishers.ofString(description))
                    .header("Content-Type", "application/json")
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "compare_info")
    public void listenCompareInfo(String message) {
        try {
            long start = System.currentTimeMillis();
            CountryNewsMessage msg = objectMapper.readValue(message, new TypeReference<>() {
            });
            String keyword = msg.getKeyword();
            String keywordMind = msg.getKeywordMind();
            String country1 = G20_COUNTRIES.get(msg.getCountry1());
            String country2 = G20_COUNTRIES.get(msg.getCountry2());
            String requestId = msg.getRequestId();
            String callbackUrl = msg.getCallbackUrl();
            String category = msg.getCategory();
            int period = msg.getPeriod();

            String prompt = buildComparePrompt(
                    keyword, keywordMind,
                    country1, country2, period, category
            );
            String summary = gptClient.ask(prompt);
            // String summary = "결과";

            // 콜백 요청 전송

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(callbackUrl + "?requestId=" + requestId))
                    .POST(HttpRequest.BodyPublishers.ofString(summary))
                    .header("Content-Type", "application/json")
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            long end = System.currentTimeMillis();
            System.out.println("뉴스 " + 3 + "개 ====> 지피티 비교 통계 시간: " + (end - start) + "ms");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "news_modal")
    public void listenNewsModal(String message) {
        try {
            long start = System.currentTimeMillis();
            Map<String, Object> payload = objectMapper.readValue(message, new TypeReference<>() {
            });

            List<String> newsIds = (List<String>) payload.get("newsIds");
            int page = (Integer) payload.get("page");
            int size = (Integer) payload.get("size");
            String callbackUrl = payload.get("callbackUrl").toString();
            String requestId = payload.get("requestId").toString();
            boolean isKorea = (Boolean) payload.get("isKorea");

            List<ForeignNewsMongo> newsList = repository(isKorea).findByIdIn(newsIds);

            NewsModalResponse response = processNews(newsList, page, size);
            String responseJson = objectMapper.writeValueAsString(response);

            // 콜백 요청 전송
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(callbackUrl + "?requestId=" + requestId))
                    .POST(HttpRequest.BodyPublishers.ofString(responseJson))
                    .header("Content-Type", "application/json")
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            long end = System.currentTimeMillis();
            int newsSize = newsIds.size();
            System.out.println("뉴스 " + newsSize + "개 ====> 뉴스 모달 리스트 통계 시간: " + (end - start) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 언론 반응 요약
    private String makeDescription(String keyword, String keywordMind,
                                   String countryName, int period) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("[국가 뉴스 여론 분석 요청]\n\n");
        prompt.append("다음은 \"").append(keyword);
        if (keywordMind != null && !keywordMind.isBlank()) {
            prompt.append("와 ").append(keywordMind);
        }
        prompt.append("\" 키워드와 관련된 ").append(countryName);
        prompt.append(" 뉴스를 참고하여, ")
                .append(countryName).append("에서 ")
                .append("오늘을 기준으로 ").append(period).append("일전까지")
                .append(keyword)
                .append("에 대해 어떤 여론이 나타나는지 세 문장으로 간략히 요약해 주세요.(1,2,3 이렇게 말고 그냥 한번에")
                .append("단, 여론이 언제 형성되었는지에 대한 기간 정보(예: '최근 30일간', '최근 며칠간')는 절대 포함하지 마세요. ");
        return prompt.toString();
    }

    // 하루치 감정 분석 (4시간 단위)
    private List<SentimentResponse> processDailySentiment(List<ForeignNewsMongo> newsList) {
        Map<LocalDateTime, List<ForeignNewsMongo>> groups = new HashMap<>();
        for (ForeignNewsMongo news : newsList) {
            LocalDateTime publishedAt = news.getPublishedAt();
            // 3시간 기준 시작 시간 구하기 (ex. 0, 4, 8 ...)
            int groupHour = (publishedAt.getHour() / 4) * 4;
            // 각 기사를 시작 기준 시간으로 설정
            LocalDateTime groupTime = publishedAt.withHour(groupHour)
                    .withMinute(0).withSecond(0).withNano(0);
            groups.computeIfAbsent(groupTime, k -> new ArrayList<>()).add(news);
        }

        List<SentimentResponse> result = new ArrayList<>();
        for (Map.Entry<LocalDateTime, List<ForeignNewsMongo>> entry : groups.entrySet()) {
            LocalDateTime groupTime = entry.getKey();
            List<ForeignNewsMongo> groupNews = entry.getValue();
            int total = groupNews.size();
            int positive = 0, neutral = 0, negative = 0;
            for (ForeignNewsMongo news : groupNews) {
                int score = news.getSentiment();
                if (score <= 33) {
                    negative++;
                } else if (score <= 66) {
                    neutral++;
                } else {
                    positive++;
                }
            }

            double posRatio = total > 0 ? (double) positive / total : 0.0;
            double neuRatio = total > 0 ? (double) neutral / total : 0.0;
            double negRatio = total > 0 ? (double) negative / total : 0.0;

            posRatio = Math.round(posRatio * 100.0) / 100.0;
            neuRatio = Math.round(neuRatio * 100.0) / 100.0;
            negRatio = Math.round(negRatio * 100.0) / 100.0;

            result.add(SentimentResponse.builder()
                    .publishedAt(groupTime)
                    .positive(posRatio)
                    .neutral(neuRatio)
                    .negative(negRatio)
                    .build());
        }
        result.sort(Comparator.comparing(SentimentResponse::getPublishedAt));
        return result;
    }

    // 일주일치 감정 분석
    private List<SentimentResponse> processWeeklySentiment(List<ForeignNewsMongo> newsList) {
        Map<LocalDateTime, List<ForeignNewsMongo>> groups = new HashMap<>();
        for (ForeignNewsMongo news : newsList) {
            LocalDateTime publishedAt = news.getPublishedAt();
            // 발행 시간을 해당 날짜 00:00:00으로 셋팅
            LocalDateTime day = publishedAt.withHour(0).withMinute(0).withSecond(0).withNano(0);
            groups.computeIfAbsent(day, k -> new ArrayList<>()).add(news);
        }

        List<SentimentResponse> result = new ArrayList<>();
        for (Map.Entry<LocalDateTime, List<ForeignNewsMongo>> entry : groups.entrySet()) {
            LocalDateTime day = entry.getKey();
            List<ForeignNewsMongo> groupNews = entry.getValue();
            int total = groupNews.size();
            int positive = 0, neutral = 0, negative = 0;
            for (ForeignNewsMongo news : groupNews) {
                int score = news.getSentiment();
                if (score <= 33) {
                    negative++;
                } else if (score <= 66) {
                    neutral++;
                } else {
                    positive++;
                }
            }

            double posRatio = total > 0 ? (double) positive / total : 0.0;
            double neuRatio = total > 0 ? (double) neutral / total : 0.0;
            double negRatio = total > 0 ? (double) negative / total : 0.0;

            posRatio = Math.round(posRatio * 100.0) / 100.0;
            neuRatio = Math.round(neuRatio * 100.0) / 100.0;
            negRatio = Math.round(negRatio * 100.0) / 100.0;

            result.add(SentimentResponse.builder()
                    .publishedAt(day)
                    .positive(posRatio)
                    .neutral(neuRatio)
                    .negative(negRatio)
                    .build());
        }
        result.sort(Comparator.comparing(SentimentResponse::getPublishedAt));
        return result;
    }

    // 한달치 감정 분석
    private List<SentimentResponse> processMonthlySentiment(List<ForeignNewsMongo> newsList) {
        Map<LocalDateTime, List<ForeignNewsMongo>> groups = new HashMap<>();
        for (ForeignNewsMongo news : newsList) {
            LocalDateTime publishedAt = news.getPublishedAt();
            // 해당 날짜가 속한 주의 시작(월요일 00:00) 계산
            LocalDateTime weekStart = publishedAt.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    .withHour(0).withMinute(0).withSecond(0).withNano(0);
            groups.computeIfAbsent(weekStart, k -> new ArrayList<>()).add(news);
        }

        List<SentimentResponse> result = new ArrayList<>();
        for (Map.Entry<LocalDateTime, List<ForeignNewsMongo>> entry : groups.entrySet()) {
            LocalDateTime weekStart = entry.getKey();
            List<ForeignNewsMongo> groupNews = entry.getValue();
            int total = groupNews.size();
            int positive = 0, neutral = 0, negative = 0;
            for (ForeignNewsMongo news : groupNews) {
                int score = news.getSentiment();
                if (score <= 33) {
                    negative++;
                } else if (score <= 66) {
                    neutral++;
                } else {
                    positive++;
                }
            }

            double posRatio = total > 0 ? (double) positive / total : 0.0;
            double neuRatio = total > 0 ? (double) neutral / total : 0.0;
            double negRatio = total > 0 ? (double) negative / total : 0.0;

            posRatio = Math.round(posRatio * 100.0) / 100.0;
            neuRatio = Math.round(neuRatio * 100.0) / 100.0;
            negRatio = Math.round(negRatio * 100.0) / 100.0;

            result.add(SentimentResponse.builder()
                    .publishedAt(weekStart)
                    .positive(posRatio)
                    .neutral(neuRatio)
                    .negative(negRatio)
                    .build());
        }
        result.sort(Comparator.comparing(SentimentResponse::getPublishedAt));
        return result;
    }

    // 하루치 언급량 분석
    private List<MentionResponse> processDailyMentions(List<ForeignNewsMongo> newsList) {
        Map<LocalDateTime, Integer> counts = new HashMap<>();
        for (ForeignNewsMongo news : newsList) {
            LocalDateTime publishedAt = news.getPublishedAt();
            int groupHour = (publishedAt.getHour() / 4) * 4;
            LocalDateTime groupTime = publishedAt.withHour(groupHour)
                    .withMinute(0).withSecond(0).withNano(0);
            counts.put(groupTime, counts.getOrDefault(groupTime, 0) + 1);
        }

        return counts.entrySet().stream()
                .map(entry -> MentionResponse.builder()
                        .publishedAt(entry.getKey())
                        .count(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(MentionResponse::getPublishedAt))
                .collect(Collectors.toList());
    }

    // 일주일치 언급량 분석
    private List<MentionResponse> processWeeklyMentions(List<ForeignNewsMongo> newsList) {
        Map<LocalDateTime, Integer> counts = new HashMap<>();
        for (ForeignNewsMongo news : newsList) {
            LocalDateTime publishedAt = news.getPublishedAt();
            LocalDateTime day = publishedAt.withHour(0).withMinute(0).withSecond(0).withNano(0);
            counts.put(day, counts.getOrDefault(day, 0) + 1);
        }

        return counts.entrySet().stream()
                .map(entry -> MentionResponse.builder()
                        .publishedAt(entry.getKey())
                        .count(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(MentionResponse::getPublishedAt))
                .collect(Collectors.toList());
    }

    // 한달치 언급량 분석
    private List<MentionResponse> processMonthlyMentions(List<ForeignNewsMongo> newsList) {
        Map<LocalDateTime, Integer> counts = new HashMap<>();
        for (ForeignNewsMongo news : newsList) {
            LocalDateTime publishedAt = news.getPublishedAt();
            LocalDateTime weekStart = publishedAt.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    .withHour(0).withMinute(0).withSecond(0).withNano(0);
            counts.put(weekStart, counts.getOrDefault(weekStart, 0) + 1);
        }

        return counts.entrySet().stream()
                .map(entry -> MentionResponse.builder()
                        .publishedAt(entry.getKey())
                        .count(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(MentionResponse::getPublishedAt))
                .collect(Collectors.toList());
    }

    // 기사 목록: 제목, URL, 발행일, 이미지 URL이 있는 뉴스 중 최신순 상위 5건 선택 위에서 이미 정렬
    private List<ArticleResponse> processArticles(List<ForeignNewsMongo> newsList) {
        return newsList.stream()
                .filter(news -> news.getTitle() != null && news.getUrl() != null)
                .limit(5)
                .map(news -> {
                    String imageUrl = news.getImageUrl();
                    List<String> keywords = news.getKeywords();
                    // 이미지 URL이 비어있으면 키워드 기반으로 카카오 이미지 검색
                    imageUrl = getKakaoImage(imageUrl, keywords);

                    return ArticleResponse.builder()
                            .title(news.getTitle())
                            .url(news.getUrl())
                            .publishedAt(news.getPublishedAt())
                            .imageUrl(imageUrl)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // 영상 목록
    private List<VideoResponse> processVideos(String keyword, String keywordMind, String country) {

        return youTubeClient.searchVideos(keyword, keywordMind, country);
    }

    //  GPT 한줄 요약
    public String buildComparePrompt(
            String keyword, String keywordMind,
            String country1, String country2, int period, String category
    ) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("[국가별 뉴스 여론 비교 요약 요청]\n\n");

        prompt.append("아래는 \"").append(keyword);
        if (keywordMind != null && !keywordMind.isBlank()) {
            prompt.append("\"와 \"").append(keywordMind);
        }
        prompt.append("\" 키워드에 대한 ").append(country1).append("와 ").append(country2).append("의 뉴스 여론 비교 요청입니다.\n");
        prompt.append("오늘부터 ").append(period).append("기간전까지");
        prompt.append(category).append("카테고리에 대해");
        prompt.append("각 국가가 이 키워드에 대해 어떤 입장, 전략, 시각을 가지고 있는지 분석하여,\n");
        prompt.append("두 국가의 입장을 각각 나열하지 말고 비교된 내용을 한 문장으로 통합해서 요약해 주세요.\n\n");

        prompt.append("출력 조건:\n");
        prompt.append("- 반드시 **한국어로 작성**해 주세요.\n");
        prompt.append(" 여론이 언제 형성되었는지에 대한 기간 정보(예: '최근 30일간', '최근 며칠간')는 절대 포함하지 마세요. ");
        prompt.append("- **한 문장**으로 간결하게 정리해 주세요.\n");
        prompt.append("- 문장은 중립적이고 비교 중심으로 구성해 주세요.\n");

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
                    List<String> keywords = new ArrayList<>();
                    keywords.add(String.valueOf(item.getSentiment()));
                    List<String> subKeywords = item.getKeywords();
                    int length = Math.min(5, subKeywords.size());
                    if (subKeywords != null) {
                        for (int i = 0; i < length; i++) {
                            keywords.add(subKeywords.get(i));
                        }
                    }
                    String imageUrl = item.getImageUrl();
                    imageUrl = getKakaoImage(imageUrl, keywords);

                    return NewsDto.builder()
                            .newsId(item.getId())
                            .title(item.getTitle())
                            .url(item.getUrl())
                            .publishedAt(item.getPublishedAt())
                            .imageUrl(imageUrl)
                            .keywords(keywords)
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

    public String getKakaoImage(String imageUrl, List<String> keywords) {
        if (imageUrl.isEmpty()) {
            if (keywords != null && !keywords.isEmpty()) {
                // 키워드 2개까지만 사용해서 검색어 구성
                String query = keywords.stream()
                        .limit(2)
                        .collect(Collectors.joining(" "));
                imageUrl = kakaoClient.searchImageUrl(query);
            }
        }
        return imageUrl;
    }

}
