// File: src/main/java/com/ssafy/userserver/application/scrap/ScrapService.java
package com.ssafy.userserver.application.scrap;

import com.ssafy.userserver.domain.dto.NewsResponse;
import com.ssafy.userserver.domain.dto.ScrapNewsListResponse;
import com.ssafy.userserver.domain.dto.ToggleScrapResponse;
import com.ssafy.userserver.domain.entity.Scrap;
import com.ssafy.userserver.domain.entity.User;
import com.ssafy.userserver.domain.enums.ScrapType;
import com.ssafy.userserver.domain.repository.ScrapRepository;
import com.ssafy.userserver.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class ScrapService {

	private final ScrapRepository scrapRepository;
	private final UserRepository userRepository;
	private final MongoTemplate mongoTemplate;
	private final RedisTemplate<String, Object> redisTemplate;
	private final TaskScheduler taskScheduler;

	// 사용자별 (userId:newsId) 예약된 동기화 작업 보관
	private final ConcurrentHashMap<String, ScheduledFuture<?>> pendingSyncTasks = new ConcurrentHashMap<>();

	// Redis key prefix: 사용자별 스크랩 Sorted Set (멤버: newsId, score: 타임스탬프)
	private static final String SCRAP_KEY_PREFIX = "scrap:";

	public ScrapService(ScrapRepository scrapRepository, UserRepository userRepository, MongoTemplate mongoTemplate,
		RedisTemplate<String, Object> redisTemplate, TaskScheduler taskScheduler) {
		this.scrapRepository = scrapRepository;
		this.userRepository = userRepository;
		this.mongoTemplate = mongoTemplate;
		this.redisTemplate = redisTemplate;
		this.taskScheduler = taskScheduler;
	}

	/**
	 * 스크랩 토글 (추가/삭제)
	 * - Redis에 즉시 반영하고, 30초 후 DB 동기화 작업을 예약합니다.
	 * - 이미 예약된 작업이 있다면 취소 후 재예약합니다.
	 */
	@Transactional
	public ToggleScrapResponse toggleScrap(String newsId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = ((com.ssafy.userserver.infrastructure.security.CustomOAuth2User) auth.getPrincipal()).getEmail();
		User user = userRepository.findByEmail(email);
		if (user == null) {
			throw new RuntimeException("해당 이메일의 사용자가 존재하지 않습니다.");
		}
		String redisKey = SCRAP_KEY_PREFIX + user.getId();
		ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
		Double score = zSetOps.score(redisKey, newsId);
		boolean finalState;
		if (score != null) {
			// 이미 스크랩되어 있다면 Redis에서 제거 (토글 → 삭제)
			zSetOps.remove(redisKey, newsId);
			finalState = false;
		} else {
			// 없으면 Redis에 추가 (토글 → 추가)
			zSetOps.add(redisKey, newsId, (double) System.currentTimeMillis());
			finalState = true;
		}
		// 예약 키: "userId:newsId"
		String taskKey = user.getId() + ":" + newsId;
		// 기존에 예약된 작업이 있다면 취소
		ScheduledFuture<?> existingTask = pendingSyncTasks.get(taskKey);
		if (existingTask != null) {
			existingTask.cancel(false);
		}
		// 30초 후에 DB 동기화 작업 예약
		ScheduledFuture<?> future = taskScheduler.schedule(() -> syncScrap(user, newsId, redisKey),
			new Date(System.currentTimeMillis() + 1000));
		pendingSyncTasks.put(taskKey, future);

		return ToggleScrapResponse.builder().isScrap(finalState).build();
	}

	/**
	 * 예약된 DB 동기화 작업:
	 * Redis의 최종 상태에 따라 DB의 scraps 테이블을 업데이트합니다.
	 */
	@Transactional
	protected void syncScrap(User user, String newsId, String redisKey) {
		// 예약 작업 제거
		String taskKey = user.getId() + ":" + newsId;
		pendingSyncTasks.remove(taskKey);
		ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
		Double score = zSetOps.score(redisKey, newsId);
		Optional<Scrap> dbRecord = scrapRepository.findByUserAndNewsId(user, newsId);
		if (score != null) { // Redis에 존재 → 스크랩되어야 함
			if (dbRecord.isEmpty()) {
				// MongoDB에서 뉴스 정보 조회하여 스크랩 타입 결정
				Query query = new Query(Criteria.where("id").is(newsId));
				Document domesticDoc = mongoTemplate.findOne(query, Document.class, "domestic_news");
				ScrapType scrapType;
				if (domesticDoc != null) {
					scrapType = ScrapType.domestic;
				} else {
					Document foreignDoc = mongoTemplate.findOne(query, Document.class, "foreign_news");
					scrapType = (foreignDoc != null) ? ScrapType.foreign : ScrapType.domestic;
				}
				Scrap scrap = Scrap.builder()
					.user(user)
					.newsId(newsId)
					.type(scrapType)
					.createdAt(LocalDateTime.now())
					.build();
				scrapRepository.save(scrap);
			}
		} else { // Redis에 없음 → DB에 스크랩 레코드가 있다면 삭제
			dbRecord.ifPresent(scrapRepository::delete);
		}
	}
	/**
	 * 스크랩 뉴스 목록 조회 (페이징)
	 * **목록 조회는 Redis가 아닌 MySQL DB의 scraps 테이블에서 페이징 처리하여 가져옵니다.**
	 * 각 스크랩의 뉴스 ID를 이용해 MongoDB에서 상세 정보를 조회합니다.
	 */
	public ScrapNewsListResponse getScrapNews(int page, int size) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = ((com.ssafy.userserver.infrastructure.security.CustomOAuth2User) auth.getPrincipal()).getEmail();
		User user = userRepository.findByEmail(email);

		PageRequest pageable = PageRequest.of(page - 1, size);
		Page<Scrap> scrapPage = scrapRepository.findAllByUserOrderByCreatedAtDesc(user, pageable);

		List<NewsResponse> newsList = scrapPage.getContent().stream()
			.map(scrap -> getNewsDetails(scrap.getNewsId()))
			.collect(Collectors.toList());

		return ScrapNewsListResponse.builder()
			.news(newsList)
			.page(page)
			.size(size)
			.totalElements(scrapPage.getTotalElements())
			.totalPages(scrapPage.getTotalPages())
			.hasNext(scrapPage.hasNext())
			.hasPrevious(scrapPage.hasPrevious())
			.build();
	}

	/**
	 * MongoDB에서 뉴스 상세 정보 조회
	 * - domestic_news: country 고정 "KR"
	 * - foreign_news: collection의 country 필드 사용
	 */
	private NewsResponse getNewsDetails(String newsId) {
		Query query = new Query(Criteria.where("id").is(newsId));
		Document domesticDoc = mongoTemplate.findOne(query, Document.class, "domestic_news");
		if (domesticDoc != null) {
			String title = domesticDoc.getString("title");
			String url = domesticDoc.getString("url");
			String publishedAtStr = domesticDoc.getString("published_at");
			LocalDateTime publishedAt = LocalDateTime.parse(publishedAtStr);
			List<String> keywords = (List<String>) domesticDoc.get("keywords");
			String imageUrl = domesticDoc.getString("image_url");
			return NewsResponse.builder()
				.newsId(newsId)
				.title(title)
				.url(url)
				.publishedAt(publishedAt)
				.country("KR")
				.keywords(keywords)
				.isScrap(true)
				.imageUrl(imageUrl)
				.build();
		} else {
			Document foreignDoc = mongoTemplate.findOne(query, Document.class, "foreign_news");
			if (foreignDoc != null) {
				String title = foreignDoc.getString("title");
				String url = foreignDoc.getString("url");
				String publishedAtStr = foreignDoc.getString("published_at");
				LocalDateTime publishedAt = LocalDateTime.parse(publishedAtStr);
				List<String> keywords = (List<String>) foreignDoc.get("keywords");
				String imageUrl = foreignDoc.getString("image_url");
				String country = foreignDoc.getString("country");
				return NewsResponse.builder()
					.newsId(newsId)
					.title(title)
					.url(url)
					.publishedAt(publishedAt)
					.country(country)
					.keywords(keywords)
					.isScrap(true)
					.imageUrl(imageUrl)
					.build();
			} else {
				return NewsResponse.builder()
					.newsId(newsId)
					.title("News not found")
					.url("")
					.publishedAt(LocalDateTime.now())
					.country("")
					.keywords(List.of())
					.isScrap(true)
					.imageUrl("")
					.build();
			}
		}
	}
}
