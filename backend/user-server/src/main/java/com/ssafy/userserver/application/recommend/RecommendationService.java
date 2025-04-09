package com.ssafy.userserver.application.recommend;

import com.ssafy.userserver.domain.entity.Log;
import com.ssafy.userserver.domain.entity.RecommendedNews;
import com.ssafy.userserver.domain.entity.User;
import com.ssafy.userserver.domain.repository.LogRepository;
import com.ssafy.userserver.domain.repository.RecommendedNewsRepository;
import com.ssafy.userserver.domain.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

	private static final Logger log = LoggerFactory.getLogger(RecommendationService.class);

	private final UserRepository userRepository;
	private final LogRepository logRepository;
	private final RecommendedNewsRepository recommendedNewsRepository;
	private final MongoTemplate mongoTemplate;

	public RecommendationService(UserRepository userRepository,
		LogRepository logRepository,
		RecommendedNewsRepository recommendedNewsRepository,
		MongoTemplate mongoTemplate) {
		this.userRepository = userRepository;
		this.logRepository = logRepository;
		this.recommendedNewsRepository = recommendedNewsRepository;
		this.mongoTemplate = mongoTemplate;
	}

	// 내부 클래스 – MongoDB에서 조회한 후보 뉴스 정보를 담는 객체
	public static class CandidateNews {
		private String newsId;
		private List<String> keywords;
		private LocalDateTime publishedAt;
		private String title;
		private String url;

		public CandidateNews(String newsId, List<String> keywords, LocalDateTime publishedAt, String title, String url) {
			this.newsId = newsId;
			this.keywords = keywords;
			this.publishedAt = publishedAt;
			this.title = title;
			this.url = url;
		}

		public String getNewsId() {
			return newsId;
		}

		public List<String> getKeywords() {
			return keywords;
		}

		public LocalDateTime getPublishedAt() {
			return publishedAt;
		}

		public String getTitle() {
			return title;
		}

		public String getUrl() {
			return url;
		}
	}

	/**
	 * 사용자 한 명에 대해 추천 생성.
	 * 로직 개요:
	 * 1. 사용자의 최근 7일간 클릭/검색 로그에서 관심 키워드를 추출.
	 * 2. MongoDB의 domestic_news, foreign_news 컬렉션에서 [now-48h, now-24h] 뉴스 후보 조회.
	 * 3. 최근 3일 내에 이미 추천된 뉴스는 후보에서 제외.
	 * 4. 각 후보 뉴스에 대해 사용자의 키워드와의 Jaccard 유사도 계산.
	 * 5. 유사도 + 소량의 랜덤 노이즈로 정렬한 후 상위 3개 선택.
	 * 6. 선택된 뉴스는 추천 내역(RecommendedNews)로 저장.
	 */
	@Transactional
	public void generateRecommendationsForUser(User user) {
		log.info("추천 생성 시작 - 사용자: {}", user.getEmail());

		// 1. 사용자 관심 키워드 추출
		List<String> userKeywords = extractUserInterestKeywords(user);
		log.info("사용자 관심 키워드 추출 결과: {}", userKeywords);
		if (userKeywords.isEmpty()) {
			log.info("7일 내에 클릭 로그가 없어 추천 진행하지 않음.");
			return;
		}

		// 2. 후보 뉴스 조회: 여기서는 최근 72시간 전부터 현재까지로 변경함 (예제)
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startTime = now.minusHours(72);
		LocalDateTime endTime = now;
		log.info("후보 뉴스 조회 - 검색 기간: {} ~ {}", startTime, endTime);
		List<CandidateNews> candidates = queryCandidateNews(startTime, endTime);
		log.info("후보 뉴스 조회 결과: {}건 발견", candidates.size());
		if (candidates.isEmpty()) {
			log.info("후보 뉴스 없음. 추천 종료.");
			return;
		}

		// 3. 최근 3일 내에 이미 추천된 뉴스는 후보에서 제외
		LocalDateTime limitTime = now.minusDays(3);
		candidates = candidates.stream()
			.filter(candidate -> {
				boolean notRecommended = recommendedNewsRepository
					.findByUserAndNewsIdAndRecommendedAtAfter(user, candidate.getNewsId(), limitTime)
					.isEmpty();
				if (!notRecommended) {
					log.info("뉴스 {} 는 최근 3일 내에 이미 추천됨.", candidate.getNewsId());
				}
				return notRecommended;
			})
			.collect(Collectors.toList());
		log.info("최근 3일 추천 뉴스 제외 후 후보 뉴스 수: {}", candidates.size());
		if (candidates.isEmpty()) {
			log.info("필터링 후 후보 뉴스 없음. 추천 종료.");
			return;
		}

		// 4. 각 후보 뉴스와 사용자의 관심 키워드 간 Jaccard 유사도 계산
		Map<CandidateNews, Double> similarityMap = new HashMap<>();
		for (CandidateNews candidate : candidates) {
			double similarity = computeJaccardSimilarity(userKeywords, candidate.getKeywords());
			similarityMap.put(candidate, similarity);
			log.info("뉴스 {} 의 유사도: {}", candidate.getNewsId(), similarity);
		}

		// 5. 유사도 + 약간의 랜덤 노이즈를 더해 내림차순 정렬 (다양성 확보)
		List<CandidateNews> sortedCandidates = similarityMap.entrySet().stream()
			.sorted((e1, e2) -> Double.compare(
				e2.getValue() + Math.random() * 0.01,
				e1.getValue() + Math.random() * 0.01))
			.map(Map.Entry::getKey)
			.collect(Collectors.toList());
		log.info("정렬 완료 후 후보 뉴스 순서:");
		for (CandidateNews candidate : sortedCandidates) {
			log.info("뉴스 {} - 제목: {}, 유사도: {}", candidate.getNewsId(), candidate.getTitle(), similarityMap.get(candidate));
		}

		// 6. 상위 3개 뉴스 선택 (3개 미만이면 가능한 만큼 선택)
		int recommendationCount = Math.min(3, sortedCandidates.size());
		List<CandidateNews> selectedRecommendations = sortedCandidates.subList(0, recommendationCount);
		log.info("최종 추천 뉴스 수: {}건, 선택된 뉴스 ID들: {}", recommendationCount,
			selectedRecommendations.stream().map(CandidateNews::getNewsId).collect(Collectors.toList()));

		// 7. 추천 내역 저장
		for (CandidateNews candidate : selectedRecommendations) {
			RecommendedNews recommendation = RecommendedNews.of(user, candidate.getNewsId(), now);
			recommendedNewsRepository.save(recommendation);
			log.info("추천 뉴스 저장 완료 - 뉴스 ID: {}", candidate.getNewsId());
		}

		log.info("추천 생성 완료 - 사용자: {}", user.getEmail());
	}

	/**
	 * 사용자의 관심 키워드 추출.
	 * 예시로 최근 7일간 사용자가 클릭한 뉴스의 "keywords" 값을 MongoDB에서 조회하여 빈도수 높은 상위 5개 키워드를 산출.
	 */
	private List<String> extractUserInterestKeywords(User user) {
		LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
		List<Log> logs = logRepository.findTop10ByUserOrderByVisitedAtDesc(user);
		List<Log> recentLogs = logs.stream()
			.filter(log -> log.getVisitedAt() != null && log.getVisitedAt().isAfter(sevenDaysAgo))
			.collect(Collectors.toList());
		log.info("최근 7일 내 로그 수: {}", recentLogs.size());

		Map<String, Integer> keywordFrequency = new HashMap<>();
		for (Log log : recentLogs) {
			String newsId = log.getNewsId();
			Query query = new Query(Criteria.where("id").is(newsId));
			Map<String, Object> newsDoc = mongoTemplate.findOne(query, Map.class, "domestic_news");
			if (newsDoc == null) {
				newsDoc = mongoTemplate.findOne(query, Map.class, "foreign_news");
			}
			if (newsDoc != null && newsDoc.get("keywords") instanceof List) {
				List<String> keywords = (List<String>) newsDoc.get("keywords");
				for (String keyword : keywords) {
					keywordFrequency.put(keyword, keywordFrequency.getOrDefault(keyword, 0) + 1);
				}
			}
		}
		// 빈도수 높은 상위 5개 키워드 반환
		List<String> topKeywords = keywordFrequency.entrySet().stream()
			.sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
			.limit(5)
			.map(Map.Entry::getKey)
			.collect(Collectors.toList());
		log.info("상위 5개 키워드: {}", topKeywords);
		return topKeywords;
	}

	/**
	 * 후보 뉴스 조회.
	 * MongoDB의 domestic_news와 foreign_news 컬렉션에서 published_at이 [startTime, endTime]에 해당하는 뉴스 조회.
	 */
	private List<CandidateNews> queryCandidateNews(LocalDateTime startTime, LocalDateTime endTime) {
		List<CandidateNews> candidates = new ArrayList<>();
		Query query = new Query();
		// published_at은 ISO 형식의 문자열로 저장되어 있다고 가정
		query.addCriteria(Criteria.where("published_at")
			.gte(startTime.toString())
			.lte(endTime.toString()));
		log.info("MongoDB Query: {}", query);

		// domestic_news 조회
		List<Map> domesticNews = mongoTemplate.find(query, Map.class, "domestic_news");
		for (Map doc : domesticNews) {
			String newsId = (String) doc.get("id");
			List<String> keywords = (List<String>) doc.get("keywords");
			String title = (String) doc.get("title");
			String url = (String) doc.get("url");
			LocalDateTime publishedAt = LocalDateTime.parse((String) doc.get("published_at"));
			candidates.add(new CandidateNews(newsId, keywords, publishedAt, title, url));
		}

		// foreign_news 조회
		List<Map> foreignNews = mongoTemplate.find(query, Map.class, "foreign_news");
		for (Map doc : foreignNews) {
			String newsId = (String) doc.get("id");
			List<String> keywords = (List<String>) doc.get("keywords");
			String title = (String) doc.get("title");
			String url = (String) doc.get("url");
			LocalDateTime publishedAt = LocalDateTime.parse((String) doc.get("published_at"));
			candidates.add(new CandidateNews(newsId, keywords, publishedAt, title, url));
		}
		return candidates;
	}

	/**
	 * 두 키워드 리스트 간의 Jaccard 유사도 계산.
	 */
	private double computeJaccardSimilarity(List<String> list1, List<String> list2) {
		if (list1.isEmpty() || list2.isEmpty()) {
			return 0.0;
		}
		Set<String> set1 = new HashSet<>(list1);
		Set<String> set2 = new HashSet<>(list2);
		Set<String> intersection = new HashSet<>(set1);
		intersection.retainAll(set2);
		Set<String> union = new HashSet<>(set1);
		union.addAll(set2);
		return (double) intersection.size() / union.size();
	}

	/**
	 * 현재 사용자(Active.Y)에 대해 지난 7주간 추천된 뉴스 내역을 조회.
	 */
	public List<RecommendedNews> getRecommendationsForUser(User user) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime sevenWeeksAgo = now.minusWeeks(7);
		return recommendedNewsRepository.findByUserAndRecommendedAtBetween(user, sevenWeeksAgo, now);
	}
}
