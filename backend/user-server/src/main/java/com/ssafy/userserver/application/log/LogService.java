package com.ssafy.userserver.application.log;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.ssafy.userserver.domain.repository.ScrapRepository;
import com.ssafy.userserver.domain.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class LogService {

	private final LogRepository logRepository;
	private final UserRepository userRepository;
	private final MongoTemplate mongoTemplate;
	private final ScrapRepository scrapRepository;


	public LogService(LogRepository logRepository, UserRepository userRepository, MongoTemplate mongoTemplate, ScrapRepository scrapRepository) {
		this.logRepository = logRepository;
		this.userRepository = userRepository;
		this.mongoTemplate = mongoTemplate;
		this.scrapRepository = scrapRepository;
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
		String email = ((com.ssafy.userserver.infrastructure.security.CustomOAuth2User) auth.getPrincipal()).getEmail();

		User user = userRepository.findByEmail(email);
		List<Log> logs = logRepository.findTop10ByUserOrderByVisitedAtDesc(user);

		List<String> newsIds = logs.stream()
				.map(Log::getNewsId)
				.collect(Collectors.toList());

		// 스크랩 여부 한 번에 조회
		List<String> scrappedNewsIds = scrapRepository.findByUserAndNewsIdIn(user, newsIds)
				.stream()
				.map(scrap -> scrap.getNewsId())
				.collect(Collectors.toList());

		// 국내/해외 뉴스 일괄 조회
		Query query = new Query(Criteria.where("id").in(newsIds));
		List<Document> domesticDocs = mongoTemplate.find(query, Document.class, "domestic_news");
		List<Document> foreignDocs = mongoTemplate.find(query, Document.class, "foreign_news");

		Map<String, Document> newsMap = new HashMap<>();
		for (Document doc : domesticDocs) {
			newsMap.put(doc.getString("id"), doc.append("country", "KR"));
		}
		for (Document doc : foreignDocs) {
			newsMap.put(doc.getString("id"), doc);
		}

		return logs.stream()
				.map(log -> {
					String id = log.getNewsId();
					Document doc = newsMap.get(id);

					if (doc == null) {
						return NewsResponse.builder()
								.newsId(id)
								.title("News not found")
								.url("")
								.publishedAt(LocalDateTime.now())
								.country("")
								.keywords(List.of())
								.isScrap(false)
								.imageUrl("")
								.build();
					}

					String title = doc.getString("title");
					String url = doc.getString("url");
					LocalDateTime publishedAt = LocalDateTime.parse(doc.getString("published_at"));
					List<String> keywords = (List<String>) doc.get("keywords");
					String imageUrl = doc.getString("image_url");
					String country = doc.getString("country");

					return NewsResponse.builder()
							.newsId(id)
							.title(title)
							.url(url)
							.publishedAt(publishedAt)
							.country(country)
							.keywords(keywords)
							.isScrap(scrappedNewsIds.contains(id))
							.imageUrl(imageUrl)
							.build();
				})
				.collect(Collectors.toList());
	}

}
