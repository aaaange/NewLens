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

import com.ssafy.staticsserver.infrastructure.client.GptClient;
import com.ssafy.staticsserver.infrastructure.client.YouTubeClient;
import com.ssafy.staticsserver.interfaces.country.dto.*;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.staticsserver.domain.news.model.ForeignNewsMongo;
import com.ssafy.staticsserver.domain.news.repository.ForeignNewsMongoDBRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CountryService {
	private final ObjectMapper objectMapper;
	private final ForeignNewsMongoDBRepository mongoDBRepository;
	private final GptClient gptClient;
	private final YouTubeClient youTubeClient;
	private final int GptNewsSize = 3;

	@KafkaListener(topics = "dashboard")
	public void listenDashboard(String message) {
		try {
			Map<String, Object> payload = objectMapper.readValue(message, new TypeReference<>() {
			});

			List<String> newsIds = (List<String>)payload.get("newsIds");
			int period = (Integer)payload.get("period");
			String keyword = (String)payload.get("keyword");
			String keywordMind = (String)payload.get("keyword-mind");
			String country = (String)payload.get("country");
			List<KeywordResponse> wordCloud = (List<KeywordResponse>)payload.get("wordCloud");
			String callbackUrl = payload.get("callbackUrl").toString();
			String requestId = payload.get("requestId").toString();

			List<ForeignNewsMongo> newsList = mongoDBRepository.findByIdIn(newsIds);
			newsList.sort((a, b) -> b.getPublishedAt().compareTo(a.getPublishedAt()));
			System.out.println("국가별 대시보드 처리를 위한 뉴스 리스트");
			for (ForeignNewsMongo foreignNewsMongo : newsList) {
				System.out.println(foreignNewsMongo);
			}

			// description
			String description;
			if (newsList.size() >= GptNewsSize) {
				String prompt = makeDescription(keyword, keywordMind, newsList, country);
				description = gptClient.ask(prompt);
			} else
				description = "관련된 뉴스가 없습니다.";

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
				.keywords(wordCloud)
				.description(description)
				.sentiment(sentiment)
				.mentions(mentions)
				.articles(articles)
				.videos(videos)
				.build();

			String responseJson = objectMapper.writeValueAsString(response);
			System.out.println(response);

			// 콜백 요청 전송
			HttpClient httpClient = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(callbackUrl + "?requestId=" + requestId))
				.POST(HttpRequest.BodyPublishers.ofString(responseJson))
				.header("Content-Type", "application/json")
				.build();

			httpClient.send(request, HttpResponse.BodyHandlers.ofString());
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
			String requestId = msg.getRequestId();
			String callbackUrl = msg.getCallbackUrl();
			List<ForeignNewsMongo> newsList1 = mongoDBRepository.findByIdIn(msg.getCountry1NewsIds());
			List<ForeignNewsMongo> newsList2 = mongoDBRepository.findByIdIn(msg.getCountry2NewsIds());

			String prompt = buildComparePrompt(
				keyword, keywordMind,
				country1, newsList1,
				country2, newsList2
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

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@KafkaListener(topics = "news-modal")
	public void listenNews(String message) {
		try {
			Map<String, Object> payload = objectMapper.readValue(message, new TypeReference<>() {
			});

			List<String> newsIds = (List<String>)payload.get("newsIds");
			int page = (Integer)payload.get("page");
			int size = (Integer)payload.get("size");
			String callbackUrl = payload.get("callbackUrl").toString();
			String requestId = payload.get("requestId").toString();

			System.out.println("newsIds : " + newsIds.toString());
			List<ForeignNewsMongo> newsList = mongoDBRepository.findByIdIn(newsIds);
			System.out.println("뉴스 모달창을 위한 뉴스 리스트");
			for (ForeignNewsMongo foreignNewsMongo : newsList) {
				System.out.println(foreignNewsMongo);
			}

			NewsModalResponse response = processNews(newsList, page, size);
			String responseJson = objectMapper.writeValueAsString(response);
			System.out.println(response);

			// 콜백 요청 전송
			HttpClient httpClient = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(callbackUrl + "?requestId=" + requestId))
				.POST(HttpRequest.BodyPublishers.ofString(responseJson))
				.header("Content-Type", "application/json")
				.build();

			httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 국가별 대시보드 집계 로직
	public void processDashboard(List<ForeignNewsMongo> newsList) {
	}

	// 언론 반응 요약
	private String makeDescription(String keyword, String keywordMind, List<ForeignNewsMongo> newsList,
		String country) {
		StringBuilder prompt = new StringBuilder();

		prompt.append("[국가 뉴스 여론 분석 요청]\n\n");
		prompt.append("다음은 \"").append(keyword);
		if (keywordMind != null && !keywordMind.isBlank()) {
			prompt.append(" (").append(keywordMind).append(")");
		}
		prompt.append("\" 키워드와 관련된 ").append(country).append("의 뉴스입니다.\n\n");

		prompt.append("[").append(country).append(" 뉴스]").append("\n");
		for (int i = 0; i < GptNewsSize; i++) {
			ForeignNewsMongo news = newsList.get(i);
			prompt.append(i + 1).append(". ").append(news.getTitle()).append("\n");
			prompt.append("- ").append(news.getDescription()).append("\n\n");
		}

		prompt.append("위 뉴스를 참고하여, ").append(country)
			.append("이 ").append(keyword).append("에 대해 어떤 시각/전략/관점을 가지고 있는지 세 문장으로 비교 요약해 주세요. 한국어로 작성해 주세요.");

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

			double posRatio = total > 0 ? (double)positive / total : 0.0;
			double neuRatio = total > 0 ? (double)neutral / total : 0.0;
			double negRatio = total > 0 ? (double)negative / total : 0.0;

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

			double posRatio = total > 0 ? (double)positive / total : 0.0;
			double neuRatio = total > 0 ? (double)neutral / total : 0.0;
			double negRatio = total > 0 ? (double)negative / total : 0.0;

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

			double posRatio = total > 0 ? (double)positive / total : 0.0;
			double neuRatio = total > 0 ? (double)neutral / total : 0.0;
			double negRatio = total > 0 ? (double)negative / total : 0.0;

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
			.map(news -> ArticleResponse.builder()
				.title(news.getTitle())
				.url(news.getUrl())
				.publishedAt(news.getPublishedAt())
				.imageUrl(news.getImageUrl())
				.build())
			.collect(Collectors.toList());
	}

	// 영상 목록
	private List<VideoResponse> processVideos(String keyword, String keywordMind, String country) {

		return youTubeClient.searchVideos(keyword, keywordMind, country);
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
		int totalPages = (int)Math.ceil((double)totalElements / size);

		// 현재 페이지 범위 계산
		// page는 1부터 시작한다고 가정
		int fromIndex = (page - 1) * size;
		int toIndex = Math.min(fromIndex + size, (int)totalElements);

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
