package com.ssafy.userserver.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.userserver.domain.entity.Log;
import com.ssafy.userserver.domain.entity.User;

public interface LogRepository extends JpaRepository<Log, Long> {

	List<Log> findTop10ByUserOrderByVisitedAtDesc(User user);

	Optional<Log> findByUserAndNewsId(User user, String newsId);

	List<Log> findByUserAndNewsIdIn(User user, List<String> newsIds);
}
