// File: src/main/java/com/ssafy/userserver/scheduler/RecommendationScheduler.java
package com.ssafy.userserver.scheduler;

import com.ssafy.userserver.application.recommend.RecommendationService;
import com.ssafy.userserver.domain.entity.User;
import com.ssafy.userserver.domain.enums.Active;
import com.ssafy.userserver.domain.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class RecommendationScheduler {

	private final UserRepository userRepository;
	private final RecommendationService recommendationService;

	public RecommendationScheduler(UserRepository userRepository, RecommendationService recommendationService) {
		this.userRepository = userRepository;
		this.recommendationService = recommendationService;
	}

	// 매일 6시, 12시, 18시에 모든 활성 사용자에 대해 추천 생성
	// @Scheduled(cron = "0 0 6,12,18 * * ?")
	// @Scheduled(cron = "0 0 * * * ?")
	@Scheduled(cron = "0 */5 * * * ?")
	public void scheduleRecommendations() {
		List<User> users = userRepository.findAll();
		for (User user : users) {
			if (user.getActive() == Active.Y) {
				recommendationService.generateRecommendationsForUser(user);
			}
		}
	}
}
