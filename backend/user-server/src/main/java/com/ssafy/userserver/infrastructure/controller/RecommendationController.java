// File: src/main/java/com/ssafy/userserver/infrastructure/controller/RecommendationController.java
package com.ssafy.userserver.infrastructure.controller;

import com.ssafy.userserver.application.recommend.RecommendationService;
import com.ssafy.userserver.common.dto.CommonResponse;
import com.ssafy.userserver.domain.entity.RecommendedNews;
import com.ssafy.userserver.domain.entity.User;
import com.ssafy.userserver.infrastructure.security.CustomOAuth2User;
import com.ssafy.userserver.domain.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user/recommend")
public class RecommendationController {

	private final RecommendationService recommendationService;
	private final UserRepository userRepository;

	public RecommendationController(RecommendationService recommendationService, UserRepository userRepository) {
		this.recommendationService = recommendationService;
		this.userRepository = userRepository;
	}

	/**
	 * 추천 뉴스 목록 조회 API
	 * - 지난 7주간(누적) 추천된 뉴스 내역을 반환합니다.
	 */
	@GetMapping("/news")
	public ResponseEntity<CommonResponse<List<RecommendedNews>>> getRecommendedNews(
		@AuthenticationPrincipal CustomOAuth2User customUser) {
		// CustomOAuth2User 에서 이메일 정보로 사용자 엔티티 조회
		User user = userRepository.findByEmail(customUser.getEmail());
		List<RecommendedNews> recommendations = recommendationService.getRecommendationsForUser(user);
		return ResponseEntity.ok(CommonResponse.success(recommendations));
	}
}
