// File: src/main/java/com/ssafy/userserver/domain/repository/RecommendedNewsRepository.java
package com.ssafy.userserver.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ssafy.userserver.domain.entity.RecommendedNews;
import com.ssafy.userserver.domain.entity.User;

public interface RecommendedNewsRepository extends JpaRepository<RecommendedNews, Long> {

	// 사용자의 특정 기간 내 추천 내역 조회 (예: 지난 7주)
	List<RecommendedNews> findByUserAndRecommendedAtBetween(User user, LocalDateTime from, LocalDateTime to);

	// 동일 뉴스가 최근 3일 내에 추천된 내역 조회 (추천 제한을 위해)
	List<RecommendedNews> findByUserAndNewsIdAndRecommendedAtAfter(User user, String newsId, LocalDateTime after);
}
