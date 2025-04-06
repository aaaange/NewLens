package com.ssafy.userserver.application.log;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ssafy.userserver.domain.dto.NewsResponse;
import com.ssafy.userserver.domain.entity.Log;
import com.ssafy.userserver.domain.entity.User;
import com.ssafy.userserver.domain.repository.LogRepository;
import com.ssafy.userserver.domain.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class LogService {

	private final LogRepository logRepository;
	private final UserRepository userRepository;
	private final MongoTemplate mongoTemplate;

	public LogService(LogRepository logRepository, UserRepository userRepository, MongoTemplate mongoTemplate) {
		this.logRepository = logRepository;
		this.userRepository = userRepository;
		this.mongoTemplate = mongoTemplate;
	}

	@Transactional
	public void saveLog(String newsId) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		String email = null;
		Object principal = auth.getPrincipal();
		email = ((com.ssafy.userserver.infrastructure.security.CustomOAuth2User) principal).getEmail();

		User user = userRepository.findByEmail(email);

		Optional<Log> optionalLog = logRepository.findByUserAndNewsId(user, newsId);

		if (optionalLog.isPresent()) {
			Log existingLog = optionalLog.get();
			existingLog.updateVisitedAt(LocalDateTime.now());
			logRepository.save(existingLog);
		} else {
			Log log = Log.builder()
				.user(user)
				.newsId(newsId)
				.visitedAt(LocalDateTime.now())
				.build();
			logRepository.save(log);
		}
	}

	@Transactional
	public List<NewsResponse> getRecentLogs() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		String email = null;
		Object principal = auth.getPrincipal();
		email = ((com.ssafy.userserver.infrastructure.security.CustomOAuth2User) principal).getEmail();

		User user = userRepository.findByEmail(email);

		List<Log> logs = logRepository.findTop10ByUserOrderByVisitedAtDesc(user);
		return logs.stream()
			.map(log -> getNewsDetails(log.getNewsId()))
			.collect(Collectors.toList());
	}

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
				.isScrap(false)
				.imageUrl(imageUrl)
				.build();
		} else {
			// 해외 뉴스 조회 – foreign_news 컬렉션
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
					.isScrap(false)
					.imageUrl(imageUrl)
					.build();
			} else {
				// 두 컬렉션 모두에서 조회되지 않은 경우
				return NewsResponse.builder()
					.newsId(newsId)
					.title("News not found")
					.url("")
					.publishedAt(LocalDateTime.now())
					.country("")
					.keywords(List.of())
					.isScrap(false)
					.imageUrl("")
					.build();
			}
		}
	}
}
