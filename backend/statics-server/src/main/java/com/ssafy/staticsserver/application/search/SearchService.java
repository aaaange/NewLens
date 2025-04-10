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

import com.ssafy.staticsserver.domain.news.repository.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.staticsserver.domain.news.model.ForeignNewsMongo;
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
	private final RedisTemplate<String, Object> redisTemplate;
	private final Map<String, Integer> aggregateCounts = new ConcurrentHashMap<>();
	private final ForeignNewsRepositoryImpl foreignRepo;
	private final DomesticNewsRepositoryImpl domesticRepo;
	// 공통 인터페이스로 isKorea 로 국내, 해외 레포지토리 선택
	private NewsMongoDBRepository repository(boolean isKorea) {
		return isKorea ? domesticRepo : foreignRepo;
	}

	private static final List<String> G20_COUNTRIES = Collections.unmodifiableList(Arrays.asList(
		"AR", "AU", "BR", "CA", "CN", "FR", "DE", "IN", "ID", "IT", "JP", "MX", "RU", "SA", "ZA", "KR", "TR", "GB",
		"US", "EU"));

	@KafkaListener(topics = "keyword_ranking")
	public void listenScheduleKeywordRanking(String message) {
		try {
			Map<String, Object> payload = objectMapper.readValue(message, new TypeReference<>() {});
			List<String> newsIds = (List<String>) payload.get("newsIds");
			String category = payload.get("category").toString();
			int period = (int) payload.get("period");
			boolean isKorea = (Boolean) payload.get("isKorea");
			boolean isLastBatch = Boolean.parseBoolean(payload.get("isLastBatch").toString());
			long start = System.currentTimeMillis();
			// 한국 실시간 데이터가 아닌 경우
			if(period < 100) {
				// 해당 배치의 뉴스 데이터 조회
				List<KeywordsOnly> batchNewsList = repository(isKorea).findKeywordsOnly(newsIds);
				// System.out.println("뉴스사이즈: "+ batchNewsList.size() +"period: " + period + "isKorea: " + isKorea + "category: " + category);
				// 배치별 집계 업데이트
				processKeywordRankingBatch(batchNewsList);
				long end = System.currentTimeMillis();
				int newsSize = newsIds.size();
				System.out.println("뉴스 " + newsSize + "개 ====> 대시보드 통계 시간: " + (end - start) + "ms");

				if (isLastBatch) {
					// 최종 집계 후 결과 생성
					KeywordRankingResponse finalResponse = finalizeKeywordRanking(category, period, isKorea);
					String redisKey = String.format("keyword_ranking:%s:%d:%b", category, period, isKorea);
					redisTemplate.opsForValue().set(redisKey, finalResponse);
					log.info("최종 집계 완료 및 Redis 업데이트, key: {}", redisKey);

					// 다음 요청을 위해 집계 변수 초기화
					aggregateCounts.clear();
				}
			}
			// 한국 실시간 데이터인 경우 period가 현재 시간 기준 + 100으로 넘어옴
			// ex 오전 9시 => period = 109
			else{

			}



		} catch (Exception e) {
			log.error("키워드 랭킹 메시지 처리 실패", e);
		}
	}

	// 각 배치에 대한 키워드 집계 업데이트
	private void processKeywordRankingBatch(List<KeywordsOnly> batchNewsList) {
		long start = System.currentTimeMillis();
		// 불용어 목록 설정
		Set<String> stopWords = new HashSet<>(Arrays.asList(
			"경기", "선수", "축구", "뉴스", "지역", "발표", "시작", "대통령", "경찰", "말", "세계", "리그", "대회", "팀", "클럽",
			"시즌", "승리", "스포츠", "대표", "국가", "정부", "오늘", "사람", "제공", "시장", "영화", "시리즈", "이야기", "출시",
			"음악", "예정", "정보", "비즈니스", "분석", "시장", "포털", "식사", "준비", "그룹", "방법", "나열", "도움", "타이틀",
			"일상", "사용", "ru", "회사", "제공", "발표", "오후", "발생", "사이", "사용자", "업데이트", "비디오", "텍스트", "라운드",
			"다운로드", "인터뷰", "서비스", "온라인", "진행", "계획", "영향", "박사", "기능", "개발", "기술", "포함", "도시", "업체",
			"제품", "브랜드", "식품", "산업", "가격", "포인트", "리트", "레스", "사랑", "공개", "스마트", "기자", "제안", "모델",
			"대학", "연구", "과학자", "관광객", "관광", "최고", "최신", "결과", "사진", "분야", "개최", "상승", "증가", "기업",
			"리뷰", "감독", "완벽", "생산", "설명", "발견", "효과", "가지", "지침", "혁신", "시스템", "출연", "여성", "월요일",
			"화요일" ,"수요일", "목요일", "금요일", "토요일", "일요일", "형식", "있다", "이날", "지난", "밝혔다", "방송된", "통해", "있는",
			"에서는", "자신의", "특히", "빠르게", "열린", "경기에서", "신한", "이번", "위한", "최근", "논문이", "위해", "오전", "오후",
			"헌법재판소의", "헌법재판소가", "대통령이", "정관장은", "있습니다", "다시", "있었다", "함께", "이하", "에서", "오는",
			"에는", "대한", "9일", "현지시간", "채널", "대해", "따르면", "출연했다", "전했다", "이후", "방송되는", "제목의", "라는",
			"영상이", "했다", "한다"
		));

		for (KeywordsOnly dto : batchNewsList) {
			List<String> keywords = dto.getKeywords();
			if (keywords != null) {
				for (String keyword : keywords) {
					if (stopWords.contains(keyword)) {
						continue;
					}
					// 각 키워드의 등장 횟수를 누적 업데이트
					aggregateCounts.merge(keyword, 1, Integer::sum);
				}
			}
		}
	}

	// 최종 집계 후 키워드 랭킹 생성
	private KeywordRankingResponse finalizeKeywordRanking(String category, int period, boolean isKorea) {
		// 누적 결과를 내림차순 정렬
		List<Map.Entry<String, Integer>> sortedKeywordCounts = aggregateCounts.entrySet()
			.stream()
			.sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
				.thenComparing(Map.Entry.comparingByKey()))
			.toList();

		List<KeywordRankingDto> rankingList = new ArrayList<>();
		int limit = Math.min(10, sortedKeywordCounts.size());

		// 이전 랭킹 데이터를 필요로 한다면 Redis에서 가져와서 비교하는 로직 추가 가능
		Map<String, Integer> previousRanking = new HashMap<>();
		String redisKey = String.format("keyword_ranking:%s:%d:%b", category, period, isKorea);
		Object rawPrevious = redisTemplate.opsForValue().get(redisKey);
		if (rawPrevious != null) {
			KeywordRankingResponse previousResponse = objectMapper.convertValue(rawPrevious, KeywordRankingResponse.class);
			if (previousResponse.getKeywords() != null) {
				for (KeywordRankingDto dto : previousResponse.getKeywords()) {
					previousRanking.put(dto.getName(), dto.getCount());
				}
			}
		}

		// 상위 limit 개의 키워드를 대상으로 상태(state) 결정 및 랭킹 리스트 생성
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
			List<ForeignNewsMongo> newsList = repository(false).findByIdIn(newsIds);

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


	// 키워드 랭킹 집계 로직
	public KeywordRankingResponse processKeywordRanking(List<KeywordsOnly> newsList, String category, int period,
		boolean isKorea) {
		Set<String> stopWords = new HashSet<>(Arrays.asList(
			"경기", "선수", "축구", "뉴스", "지역", "발표", "시작", "대통령", "경찰", "말", "세계", "리그", "대회", "팀", "클럽",
			"시즌", "승리", "스포츠", "대표", "국가", "정부", "오늘", "사람", "제공", "시장", "영화", "시리즈", "이야기", "출시",
			"음악", "예정", "정보", "비즈니스", "분석", "시장", "포털", "식사", "준비", "그룹", "방법", "나열", "도움", "타이틀",
			"일상", "사용", "ru", "회사", "제공", "발표", "오후", "발생", "사이", "사용자", "업데이트", "비디오", "텍스트", "라운드",
			"다운로드", "인터뷰", "서비스", "온라인", "진행", "계획", "영향", "박사", "기능", "개발", "기술", "포함", "도시", "업체",
			"제품", "브랜드", "식품", "산업", "가격", "포인트", "리트", "레스", "사랑", "공개", "스마트", "기자", "제안", "모델",
			"대학", "연구", "과학자", "관광객", "관광", "최고", "최신", "결과", "사진", "분야", "개최", "상승", "증가", "기업",
			"리뷰", "감독", "완벽", "생산", "설명", "발견", "효과", "가지", "지침", "혁신", "시스템", "출연", "여성", "함께"
		));

		// 각 키워드의 등장 횟수를 저장할 맵
		Map<String, Integer> keywordCounts = new HashMap<>();

		// 뉴스 리스트를 순회하면서 각 뉴스의 keywords를 추출
		for (KeywordsOnly dto : newsList) {
			List<String> keywords = dto.getKeywords();
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