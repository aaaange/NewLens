package com.ssafy.staticsserver.application.country;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import com.ssafy.staticsserver.infrastructure.client.YoutubeService;
import com.ssafy.staticsserver.domain.news.repository.DomesticNewsRepositoryImpl;
import com.ssafy.staticsserver.domain.news.repository.ForeignNewsRepositoryImpl;
import com.ssafy.staticsserver.domain.news.repository.NewsMongoDBRepository;
import com.ssafy.staticsserver.domain.user.entity.Scrap;
import com.ssafy.staticsserver.domain.user.entity.User;
import com.ssafy.staticsserver.domain.user.repository.ScrapRepository;
import com.ssafy.staticsserver.domain.user.repository.UserRepository;
import com.ssafy.staticsserver.infrastructure.client.GoogleClient;
import com.ssafy.staticsserver.infrastructure.client.GptClient;
import com.ssafy.staticsserver.infrastructure.client.KakaoClient;
import com.ssafy.staticsserver.interfaces.country.dto.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.staticsserver.domain.news.model.ForeignNewsMongo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CountryService {
    private final ObjectMapper objectMapper;
    private final GptClient gptClient;
    private final YoutubeService youtubeService;
    private final ForeignNewsRepositoryImpl foreignRepo;
    private final DomesticNewsRepositoryImpl domesticRepo;
    private final KakaoClient kakaoClient;
    private final GoogleClient googleClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final int PAGE_GROUP_SIZE = 5;
    private final UserRepository userRepository;
    private final ScrapRepository scrapRepository;

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
            List<ArticleResponse> articles = processArticles(newsList, isKorea);

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
            String category = (String) payload.get("category");
            int period = (Integer) payload.get("period");
            List<String> newsIds = (List<String>) payload.get("newsIds");
            boolean isKorea = (Boolean) payload.get("isKorea");
            String countryName = G20_COUNTRIES.getOrDefault(country, "한국");

            List<ForeignNewsMongo> newsList = repository(isKorea).findByIdIn(newsIds);

            String description;
            if (!newsList.isEmpty()) {
                String prompt = buildNewsSummaryPrompt(keyword, keywordMind, countryName, period, category, newsList);
                description = gptClient.ask(prompt);
            } else {
                description = "관련된 뉴스가 없습니다.";
            }


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
            boolean isKorea = msg.isKorea();

            List<String> country1NewsIds = msg.getCountry1NewsIds();
            List<String> country2NewsIds = msg.getCountry2NewsIds();

            List<ForeignNewsMongo> newsList1 = repository(isKorea).findByIdIn(country1NewsIds);
            List<ForeignNewsMongo> newsList2 = repository(isKorea).findByIdIn(country2NewsIds);

            String prompt = buildCompareNewsPrompt(
                keyword, keywordMind, country1, country2,
                newsList1, newsList2
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
            System.out.println("뉴스 " + newsList2.size() * 2 + "개 ====> 지피티 비교 통계 시간: " + (end - start) + "ms");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "news_modal")
    public void listenNewsModal(String message) {
        try {
            long start = System.currentTimeMillis();
            NewsModalRequest newsModalRequest = objectMapper.readValue(message, NewsModalRequest.class);

            List<String> newsIds = newsModalRequest.getNewsIds();
            int page = newsModalRequest.getPage();
            int size = newsModalRequest.getSize();
            String callbackUrl = newsModalRequest.getCallbackUrl();
            String requestId = newsModalRequest.getRequestId();
            boolean isKorea = newsModalRequest.isKorea();
            String keyword = newsModalRequest.getKeyword();
            String keywordMind = newsModalRequest.getKeywordMind();
            String keywordCloud = newsModalRequest.getKeywordCloud();
            String country = newsModalRequest.getCountry();
            String category = newsModalRequest.getCategory();
            int period = newsModalRequest.getPeriod();
            String email = newsModalRequest.getEmail();


//            List<ForeignNewsMongo> newsList = repository(isKorea).findByIdIn(newsIds);
            // 해시 키 생성
            int startPage = ((page - 1) / PAGE_GROUP_SIZE) * PAGE_GROUP_SIZE + 1;
            int endPage = startPage + PAGE_GROUP_SIZE - 1;
            String rawKey = String.join("|",
                keyword,
                keywordMind != null ? keywordMind : "none",
                keywordCloud != null ? keywordCloud : "none",
                category != null ? category : "all",
                country != null ? country : "all",
                String.valueOf(isKorea),
                String.valueOf(period)
            );

            String hash = DigestUtils.md5DigestAsHex(rawKey.getBytes());
            String redisKey = String.format("modal_cache:%s:%d", hash, startPage);
            // 캐시 있으면 가져옴
            Object cached = redisTemplate.opsForValue().get(redisKey);
            if (cached != null) {
                List<NewsModalResponse> cachedPages = objectMapper.convertValue(cached, new TypeReference<>() {});
                NewsModalResponse cachedResponse = cachedPages.get(page - startPage);

                Optional<User> optionalUser;
                optionalUser = userRepository.findByEmail(email);
                List<String> scrappedNewsIds;
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    List<Scrap> scraps = scrapRepository.findByUserAndNewsIdIn(user, newsIds);
                    scrappedNewsIds = scraps.stream().map(Scrap::getNewsId).toList();
                } else {
                    scrappedNewsIds = new ArrayList<>();
                }

                List<NewsDto> updatedNewsDtos = cachedResponse.getNews().stream()
                    .map(news -> NewsDto.builder()
                        .newsId(news.getNewsId())
                        .title(news.getTitle())
                        .url(news.getUrl())
                        .publishedAt(news.getPublishedAt())
                        .imageUrl(news.getImageUrl())
                        .keywords(news.getKeywords())
                        .isScrap(scrappedNewsIds.contains(news.getNewsId()))
                        .build()
                    )
                    .collect(Collectors.toList());

                NewsModalResponse updatedResponse = NewsModalResponse.builder()
                    .news(updatedNewsDtos)
                    .page(cachedResponse.getPage())
                    .size(cachedResponse.getSize())
                    .totalElements(cachedResponse.getTotalElements())
                    .totalPages(cachedResponse.getTotalPages())
                    .hasNext(cachedResponse.isHasNext())
                    .hasPrevious(cachedResponse.isHasPrevious())
                    .build();

                sendCallback(callbackUrl, requestId, updatedResponse);
                System.out.println("캐싱된: 페이지 " + page);
                return;
            }
            // 캐시 없으면 직접 조회 PAGE_GROUP 크기 만큼 캐싱
            else{
                List<NewsModalResponse> pageGroup = new ArrayList<>();
                for (int p = startPage; p <= endPage; p++) {
                    pageGroup.add(processNews(newsIds, p, size, isKorea, email));
                }
                redisTemplate.opsForValue().set(redisKey, pageGroup, Duration.ofMinutes(5));
                sendCallback(callbackUrl, requestId, pageGroup.get(page - startPage));
                System.out.println("캐시 안된: 페이지 " + page + " 캐싱 및 응답");
            }


            long end = System.currentTimeMillis();
            int newsSize = newsIds.size();
            System.out.println("뉴스 " + newsSize + "개 ====> 뉴스 모달 리스트 통계 시간: " + (end - start) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendCallback(String callbackUrl, String requestId, NewsModalResponse response) throws Exception {
        String responseJson = objectMapper.writeValueAsString(response);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(callbackUrl + "?requestId=" + requestId))
            .POST(HttpRequest.BodyPublishers.ofString(responseJson))
            .header("Content-Type", "application/json")
            .build();

        HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }


    private String googleTranslate(String originTitle, String notTranslateTitle) {
        try {
            // 원문 제목이 없으면 패스~
            if (originTitle != null && !originTitle.isBlank()) {
                return googleClient.translateText(originTitle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notTranslateTitle;
    }



    private String buildNewsSummaryPrompt(
        String keyword, String keywordMind, String countryName,
        int period, String category, List<ForeignNewsMongo> newsList
    ) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("[국가 뉴스 여론 분석 요청]\n\n");
        prompt.append("다음은 \"").append(keyword);
        if (keywordMind != null && !keywordMind.isBlank()) {
            prompt.append("\"와 \"").append(keywordMind);
        }
        prompt.append("\" 키워드와 관련된 ").append(countryName).append(" 뉴스를 참고하여, ");
        prompt.append("이 키워드에 대해 ").append(countryName)
            .append("에서 어떤 여론이 나타나는지 세 문장으로 요약해 주세요.\n");
        prompt.append("### 출력조건 ### 1,2,3 이렇게 나누지 말고 한번에 말해주세요 줄바꿈도 포함하지 마세요");
        prompt.append("절대 날짜나 카테고리 등은 포함하지 마세요.\n\n");
        prompt.append("출력 글자 수 는 반드시 공백 포함 180자 이하로 설정해주세요");

        prompt.append("관련 뉴스 목록:\n");

        newsList.stream()
            .sorted(Comparator.comparing(ForeignNewsMongo::getPublishedAt).reversed())
            .limit(50)
            .forEach(news -> {
                prompt.append("- 제목: ").append(news.getTitle()).append("\n");
                if (news.getDescription() != null && !news.getDescription().isBlank()) {
                    prompt.append("  요약: ").append(news.getDescription()).append("\n");
                }
            });

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
    private List<ArticleResponse> processArticles(List<ForeignNewsMongo> newsList, boolean isKorea) {
        return newsList.stream()
                .filter(news -> news.getTitle() != null && news.getUrl() != null)
                .limit(5)
                .map(news -> {
                    String imageUrl = news.getImageUrl();
                    List<String> keywords = news.getKeywords();
                    // 이미지 URL이 비어있으면 키워드 기반으로 카카오 이미지 검색
                    imageUrl = getKakaoImage(imageUrl, keywords);
                    // 원문 제목가져오고 번역 없으면 기존 번역 그대로
                    String translatedTitle = isKorea ? news.getTitle() : googleTranslate(news.getOriginTitle(), news.getTitle());

                   // String originalTitle = news.getTitle();
                   // System.out.println("번역 전 : " + originalTitle);
                   // System.out.println("번역 후: " + translatedTitle);

                    return ArticleResponse.builder()
                        .newsId(news.getId())
                            .title(translatedTitle)
                            .url(news.getUrl())
                            .publishedAt(news.getPublishedAt())
                            .imageUrl(imageUrl)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // 영상 목록
    private List<VideoResponse> processVideos(String keyword, String keywordMind, String country) {

        return youtubeService.searchVideos(keyword, keywordMind, country);
    }

    //  GPT 한줄 요약
    private String buildCompareNewsPrompt(
        String keyword, String keywordMind,
        String country1, String country2,
        List<ForeignNewsMongo> newsList1, List<ForeignNewsMongo> newsList2
    ) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("[국가별 뉴스 여론 비교 요약 요청]\n\n");
        prompt.append("다음은 \"").append(keyword);
        if (keywordMind != null && !keywordMind.isBlank()) {
            prompt.append("\"와 \"").append(keywordMind);
        }
        prompt.append("\" 키워드에 대한 ").append(country1).append("와 ").append(country2).append("의 뉴스 내용입니다.\n");
        prompt.append("각 국가가 이 키워드에 대해 어떤 입장, 전략, 시각을 가지고 있는지 비교해 주세요.\n");
        prompt.append("절대 날짜, 기간, 카테고리 정보는 포함하지 마세요. 각 국가 입장을 나열하지 말고 비교된 관점으로 1~2문장으로 간략하게 요약해 주세요. 반드시 공백포함 140 글자 이하로 해주세요\n\n");

        prompt.append("[").append(country1).append(" 뉴스 목록]\n");
        newsList1.stream()
            .sorted(Comparator.comparing(ForeignNewsMongo::getPublishedAt).reversed())
            .limit(50)
            .forEach(news -> {
                prompt.append("- 제목: ").append(news.getTitle()).append("\n");
                if (news.getDescription() != null && !news.getDescription().isBlank()) {
                    prompt.append("  요약: ").append(news.getDescription()).append("\n");
                }
            });

        prompt.append("\n[").append(country2).append(" 뉴스 목록]\n");
        newsList2.stream()
            .sorted(Comparator.comparing(ForeignNewsMongo::getPublishedAt).reversed())
            .limit(50)
            .forEach(news -> {
                prompt.append("- 제목: ").append(news.getTitle()).append("\n");
                if (news.getDescription() != null && !news.getDescription().isBlank()) {
                    prompt.append("  요약: ").append(news.getDescription()).append("\n");
                }
            });

        return prompt.toString();
    }


    // 뉴스 리스트 모달창 출력을 위한 집계 로직
    public NewsModalResponse processNews(List<String> newsIds, int page, int size, boolean isKorea, String email) {

        Pageable pageable = PageRequest.of(page -1, size, Sort.by("PublishedAt").descending());
        Page<ForeignNewsMongo> pagedNews = repository(isKorea).findByIdIn(newsIds, pageable);

        Optional<User> optionalUser;
        optionalUser = userRepository.findByEmail(email);
        List<String> scrappedNewsIds;
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            List<Scrap> scraps = scrapRepository.findByUserAndNewsIdIn(user, newsIds);
            scrappedNewsIds = scraps.stream().map(Scrap::getNewsId).toList();
        } else {
            scrappedNewsIds = new ArrayList<>();
        }


        List<NewsDto> newsDtos = pagedNews.getContent().stream()
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

                    String translatedTitle = isKorea ? item.getTitle() : googleTranslate(item.getOriginTitle(), item.getTitle());


                    return NewsDto.builder()
                            .newsId(item.getId())
                            .title(translatedTitle)
                            .url(item.getUrl())
                            .publishedAt(item.getPublishedAt())
                            .imageUrl(imageUrl)
                            .keywords(keywords)
                            .isScrap(scrappedNewsIds.contains(item.getId()))
                            .build();
                })
                .collect(Collectors.toList());


        return NewsModalResponse.builder()
                .news(newsDtos)
                .page(page)
                .size(size)
                .totalElements((int)pagedNews.getTotalElements())
                .totalPages(pagedNews.getTotalPages())
                .hasNext(pagedNews.hasNext())
                .hasPrevious(pagedNews.hasPrevious())
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
