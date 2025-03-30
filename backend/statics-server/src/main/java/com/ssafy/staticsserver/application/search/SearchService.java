package com.ssafy.staticsserver.application.search;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.staticsserver.domain.news.model.ForeignNewsMongo;
import com.ssafy.staticsserver.domain.news.repository.ForeignNewsMongoDBRepository;
import com.ssafy.staticsserver.interfaces.search.dto.KeywordRankingDto;
import com.ssafy.staticsserver.interfaces.search.dto.KeywordRankingResponse;
import com.ssafy.staticsserver.interfaces.search.dto.MentionResponse;
import com.ssafy.staticsserver.interfaces.search.dto.SentimentMentionResponse;
import com.ssafy.staticsserver.interfaces.search.dto.SentimentResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchService {

	private final ObjectMapper objectMapper;
	private final ForeignNewsMongoDBRepository mongoDBRepository;
	private final RedisTemplate<String, Object> redisTemplate;

	private static final List<String> G20_COUNTRIES = Collections.unmodifiableList(Arrays.asList(
		"AR", "AU", "BR", "CA", "CN", "FR", "DE", "IN", "ID", "IT", "JP", "MX", "RU", "SA", "ZA", "KR", "TR", "GB", "US", "EU"));


	@KafkaListener(topics = "keyword-ranking")
	public void listenKeywordRanking(String message) {
		try {
			Map<String, Object> payload = objectMapper.readValue(message, new TypeReference<>() {
			});
			List<String> newsIds = (List<String>)payload.get("newsIds");
			String callbackUrl = payload.get("callbackUrl").toString();
			String requestId = payload.get("requestId").toString();
			List<ForeignNewsMongo> newsList = mongoDBRepository.findByIdIn(newsIds);
			System.out.println("키워드 처리를 위한 뉴스 리스트");
			for (ForeignNewsMongo foreignNewsMongo : newsList) {
				System.out.println(foreignNewsMongo);
			}

			System.out.println();
			KeywordRankingResponse response = processKeywordRanking(newsList);
			String responseJson = objectMapper.writeValueAsString(response);

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

	@KafkaListener(topics = "worldwide")
	public void listenWorldwide(String message) {
		try {
			// 메시지를 Map으로 변환
			Map<String, Object> payload = objectMapper.readValue(message, new TypeReference<>() {
			});

			// keyword와 ids 추출 (ids는 List<String>으로 캐스팅)
			String keyword = (String)payload.get("keyword");
			String keywordMind = (String)payload.get("keyword-mind");
			List<String> newsIds = (List<String>)payload.get("ids");
			String callbackUrl = payload.get("callbackUrl").toString();
			String requestId = payload.get("requestId").toString();

			// MongoDB에서 해당 id에 해당하는 뉴스 조회
			List<ForeignNewsMongo> newsList = mongoDBRepository.findByIdIn(newsIds);

			// List<String> newsIds = objectMapper.readValue(message, new TypeReference<List<String>>() {
			// });

			System.out.println("세계 지도 처리를 위한 뉴스 리스트");
			System.out.println(newsIds);
			for (ForeignNewsMongo foreignNewsMongo : newsList) {
				System.out.println(foreignNewsMongo);
			}
			SentimentMentionResponse response = processWorldwide(keyword, keywordMind, newsList);
			String responseJson = objectMapper.writeValueAsString(response);

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

	// 관련 키워드 집계 로직
	public void processRelatedKeywords(List<ForeignNewsMongo> newsList) {
	}

	// 키워드 랭킹 집계 로직
	public KeywordRankingResponse processKeywordRanking(List<ForeignNewsMongo> newsList) {
		// 각 키워드의 등장 횟수를 저장할 맵 생성
		Map<String, Integer> keywordCounts = new HashMap<>();

		// 뉴스 리스트를 순회하면서 각 뉴스의 keywords를 추출
		for (ForeignNewsMongo news : newsList) {
			List<String> keywords = news.getKeywords();
			if (keywords != null) {  // null 체크
				for (String keyword : keywords) {
					keywordCounts.put(keyword, keywordCounts.getOrDefault(keyword, 0) + 1);
				}
			}
		}

		// Map의 엔트리들을 값(빈도) 기준으로 내림차순 정렬 (동일 빈도이면 키 값으로 정렬)
		List<Map.Entry<String, Integer>> sortedKeywordCounts = keywordCounts.entrySet()
			.stream()
			.sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
				.thenComparing(Map.Entry.comparingByKey()))
			.toList();

		// 5. Redis에서 기존(이전) 키워드 랭킹 정보 조회
		// Redis에 저장된 랭킹은 "keyword_ranking"이라는 해시(Hash) 자료형에 저장되어 있다고 가정
		Map<Object, Object> previousRankingMap = redisTemplate.opsForHash().entries("keyword_ranking");

		// 6. 키워드별 state 결정 및 결과 DTO 리스트 생성 (최대 10개)
		List<KeywordRankingDto> resultList = new ArrayList<>();
		int limit = Math.min(10, sortedKeywordCounts.size());
		for (int i = 0; i < limit; i++) {
			Map.Entry<String, Integer> entry = sortedKeywordCounts.get(i);
			String keyword = entry.getKey();
			int newCount = entry.getValue();
			String state = "";

			if (!previousRankingMap.containsKey(keyword)) {
				// Redis에 기존 정보가 없다면 신규 키워드로 "new"
				state = "new";
			} else {
				// 기존 빈도수와 비교하여 증가폭에 따라 "hot" 판단
				int oldCount = Integer.parseInt(previousRankingMap.get(keyword).toString());
				// 예시 조건: 이전 빈도가 있고, 새 빈도가 이전의 1.5배 이상 증가했다면 "hot"
				if (oldCount > 0 && newCount >= 1.5 * oldCount) {
					state = "hot";
				}
			}

			// KeywordRankingDto의 name 필드에는 키워드를, state에는 결정된 상태 값을 넣음
			resultList.add(new KeywordRankingDto(keyword, state));
		}

		// 7. Redis에 새로운 키워드 랭킹 업데이트 (전체 랭킹 갱신)
		// redisTemplate.opsForHash().putAll("keyword_ranking", keywordCounts);

		// 결과 출력: 키워드 랭킹
		System.out.println("키워드 랭킹 결과:");
		for (int i = 0; i < limit; i++) {
			Map.Entry<String, Integer> entry = sortedKeywordCounts.get(i);
			KeywordRankingDto dto = resultList.get(i);
			System.out.println(entry.getKey() + " : " + entry.getValue()
				+ " (state=" + dto.getState() + ")");
		}

		// 8. 최종 결과를 KeywordRankingResponse로 구성하여 반환
		return KeywordRankingResponse.builder()
			.keywords(resultList)
			.build();
	}

	// 세계지도 집계 로직
	public SentimentMentionResponse processWorldwide(String keyword, String keywordMind,
		List<ForeignNewsMongo> newsList) {

		// 국가별로 뉴스 그룹핑
		Map<String, List<ForeignNewsMongo>> countryNewsMap = new HashMap<>();
		for (ForeignNewsMongo news : newsList) {
			String country = news.getCountry();
			countryNewsMap.computeIfAbsent(country, k -> new ArrayList<>()).add(news);
		}

		List<SentimentResponse> sentimentResponses = new ArrayList<>();
		List<MentionResponse> mentionResponses = new ArrayList<>();

		// G20 국가 각각에 대해 통계 계산 (뉴스가 없으면 기본값 0 적용)
		for (String country : G20_COUNTRIES) {
			List<ForeignNewsMongo> countryNews = countryNewsMap.getOrDefault(country, new ArrayList<>());
			int totalCount = countryNews.size();
			int positiveCount = 0;
			int neutralCount = 0;
			int negativeCount = 0;

			for (ForeignNewsMongo news : countryNews) {
				int sentimentScore = news.getSentiment();
				// sentiment 값이 33 이하면 negative, 66 이하면 neutral, 그 이상이면 positive
				if (sentimentScore <= 33) {
					negativeCount++;
				} else if (sentimentScore <= 66) {
					neutralCount++;
				} else {
					positiveCount++;
				}
			}

			double positiveRatio = totalCount > 0 ? (double)positiveCount / totalCount : 0.0;
			double neutralRatio = totalCount > 0 ? (double)neutralCount / totalCount : 0.0;
			double negativeRatio = totalCount > 0 ? (double)negativeCount / totalCount : 0.0;

			positiveRatio = Math.round(positiveRatio * 100.0) / 100.0;
			neutralRatio = Math.round(neutralRatio * 100.0) / 100.0;
			negativeRatio = Math.round(negativeRatio * 100.0) / 100.0;

			// SentimentResponse 생성
			SentimentResponse sentimentResponse = SentimentResponse.builder()
				.country(country)
				.positive(positiveRatio)
				.neutral(neutralRatio)
				.negative(negativeRatio)
				.build();
			sentimentResponses.add(sentimentResponse);

			// MentionResponse 생성
			MentionResponse mentionResponse = MentionResponse.builder()
				.country(country)
				.count(totalCount)
				.build();
			mentionResponses.add(mentionResponse);
		}

		SentimentMentionResponse response = SentimentMentionResponse.builder()
			.keyword(keyword)
			.keywordMind(keywordMind)
			.sentiment(sentimentResponses)
			.mention(mentionResponses)
			.build();

		System.out.println("세계지도 집계 결과:");
		System.out.println(response);
		return response;
	}
}