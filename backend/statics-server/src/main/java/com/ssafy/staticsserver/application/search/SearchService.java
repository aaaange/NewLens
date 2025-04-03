package com.ssafy.staticsserver.application.search;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

	private final ObjectMapper objectMapper;
	private final ForeignNewsMongoDBRepository mongoDBRepository;
	private final RedisTemplate<String, Object> redisTemplate;
	private final Map<String, List<String>> idList = new ConcurrentHashMap<>();

	private static final List<String> G20_COUNTRIES = Collections.unmodifiableList(Arrays.asList(
		"AR", "AU", "BR", "CA", "CN", "FR", "DE", "IN", "ID", "IT", "JP", "MX", "RU", "SA", "ZA", "KR", "TR", "GB", "US", "EU"));

	@KafkaListener(topics = "keyword_ranking")
	public void listenScheduleKeywordRanking(String message) {
		try {
			long start = System.currentTimeMillis();
			Map<String, Object> payload = objectMapper.readValue(message, new TypeReference<>() {});
			List<String> newsIds = (List<String>) payload.get("newsIds");
			String category = payload.get("category").toString();
			int period = (int) payload.get("period");
			boolean isKorea = Boolean.parseBoolean(payload.get("isKorea").toString());

			String requestId = payload.get("requestId").toString();
			boolean isLastBatch = Boolean.parseBoolean(payload.get("isLastBatch").toString()); // 마지막 배치 여부
			idList.computeIfAbsent(requestId, k -> Collections.synchronizedList(new ArrayList<>())).addAll(newsIds);


			if(isLastBatch) {
				List<String> allNewsIds = idList.remove(requestId); // 가져오고 삭제
				System.out.println(allNewsIds.size());
				List<ForeignNewsMongo> newsList = mongoDBRepository.findByIdIn(allNewsIds); // 전체 뉴스 조회

				// 키워드 랭킹 계산 및 Redis 업데이트
				KeywordRankingResponse response = processKeywordRanking(newsList, category, period, isKorea);
				String redisKey = String.format("keyword_ranking:%s:%d:%b", category, period, isKorea);
				redisTemplate.opsForValue().set(redisKey, response);
				log.info("스케줄링: Redis 업데이트 완료, key: {}", redisKey);

				long end = System.currentTimeMillis();
				log.info("스케줄링 통계 처리 완료, 소요 시간: {}ms", (end - start));
			}
		} catch (Exception e) {
			log.error("스케줄링 메시지 처리 실패", e);
		}
	}

	@KafkaListener(topics = "worldwide")
	public void listenWorldwide(String message) {
		try {
			long start = System.currentTimeMillis();
			// 메시지를 Map으로 변환
			Map<String, Object> payload = objectMapper.readValue(message, new TypeReference<>() {
			});

			// keyword와 ids 추출 (ids는 List<String>으로 캐스팅)
			String keyword = (String)payload.get("keyword");
			String keywordMind = (String)payload.get("keyword_mind");
			List<String> newsIds = (List<String>)payload.get("ids");
			String callbackUrl = payload.get("callbackUrl").toString();
			String requestId = payload.get("requestId").toString();

			// MongoDB에서 해당 id에 해당하는 뉴스 조회
			List<ForeignNewsMongo> newsList = mongoDBRepository.findByIdIn(newsIds);


			SentimentMentionResponse response = processWorldwide(keyword, keywordMind, newsList);
			String responseJson = objectMapper.writeValueAsString(response);

			HttpClient httpClient = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(callbackUrl + "?requestId=" + requestId))
				.POST(HttpRequest.BodyPublishers.ofString(responseJson))
				.header("Content-Type", "application/json")
				.build();

			httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			long end = System.currentTimeMillis();
			int newsSize = newsIds.size();
			System.out.println("뉴스 " + newsSize + "개 ====>세계 지도 통계 시간: " + (end - start) + "ms");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 관련 키워드 집계 로직
	public void processRelatedKeywords(List<ForeignNewsMongo> newsList) {
	}

	// 키워드 랭킹 집계 로직
	public KeywordRankingResponse processKeywordRanking(List<ForeignNewsMongo> newsList, String category, int period, boolean isKorea) {
		Set<String> stopWords = new HashSet<>(Arrays.asList(
			"경기", "선수", "축구", "뉴스", "지역", "발표", "시작", "대통령", "경찰", "말", "세계", "리그", "대회", "팀", "클럽",
			"시즌", "승리", "스포츠", "대표", "국가", "정부", "오늘", "사람", "제공", "시장", "영화", "시리즈", "이야기", "출시",
			"음악", "예정", "정보", "비즈니스", "분석", "시장", "포털", "식사", "준비", "그룹", "방법", "나열", "도움", "타이틀",
			"일상", "사용", "ru", "회사", "제공", "발표", "오후", "발생", "사이", "사용자", "업데이트", "비디오", "텍스트", "라운드",
			"다운로드", "인터뷰", "서비스", "온라인", "진행", "계획", "영향", "박사", "기능", "개발", "기술", "포함", "도시", "업체",
			"제품", "브랜드", "식품", "산업", "가격", "포인트", "리트", "레스", "사랑", "공개", "스마트", "기자", "제안", "모델",
			"대학", "연구", "과학자", "관광객", "관광", "최고", "최신", "결과", "사진", "분야", "개최", "상승", "증가", "기업",
			"리뷰", "감독", "완벽", "생산", "설명", "발견", "효과", "가지", "지침", "혁신", "시스템", "출연", "여성"
		));

		// 각 키워드의 등장 횟수를 저장할 맵
		Map<String, Integer> keywordCounts = new HashMap<>();

		// 뉴스 리스트를 순회하면서 각 뉴스의 keywords를 추출
		for (ForeignNewsMongo news : newsList) {
			List<String> keywords = news.getKeywords();
			if (keywords != null) {
				for (String keyword : keywords) {
					if (stopWords.contains(keyword)) {
						continue;
					}
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

		List<KeywordRankingDto> rankingList = new ArrayList<>();
		int limit = Math.min(10, sortedKeywordCounts.size());

		// Redis에서 동일한 키로 저장된 이전 랭킹 데이터 불러오기
		String redisKey = String.format("keyword_ranking:%s:%d:%b", category, period, isKorea);
		// KeywordRankingResponse previousResponse = (KeywordRankingResponse) redisTemplate.opsForValue().get(redisKey);
		Object rawPrevious = redisTemplate.opsForValue().get(redisKey);
		KeywordRankingResponse previousResponse = null;
		if (rawPrevious != null) {
			previousResponse = objectMapper.convertValue(rawPrevious, KeywordRankingResponse.class);
		}

		// 이전 랭킹 데이터를 Map 형태로 변환 (keyword : count)
		Map<String, Integer> previousRanking = new HashMap<>();
		if (previousResponse != null && previousResponse.getKeywords() != null) {
			for (KeywordRankingDto dto : previousResponse.getKeywords()) {
				previousRanking.put(dto.getName(), dto.getCount());
			}
		}

		// 상위 limit 개의 키워드를 대상으로 state 결정
		for (int i = 0; i < limit; i++) {
			Map.Entry<String, Integer> entry = sortedKeywordCounts.get(i);
			String keyword = entry.getKey();
			int newCount = entry.getValue();
			String state = "";
			if (!previousRanking.containsKey(keyword)) {
				state = "new";
			} else {
				int oldCount = previousRanking.get(keyword);
				if (oldCount > 0 && newCount >= 1.5 * oldCount) {
					state = "hot";
				}
			}
			rankingList.add(new KeywordRankingDto(keyword, newCount, state));
		}

		return KeywordRankingResponse.builder().keywords(rankingList).build();
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

		return response;
	}
}