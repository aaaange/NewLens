package com.ssafy.userserver.domain.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.userserver.domain.entity.Scrap;
import com.ssafy.userserver.domain.entity.User;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {
	Optional<Scrap> findByUserAndNewsId(User user, String newsId);
	Page<Scrap> findAllByUserOrderByCreatedAtDesc(User user, Pageable pageable);
	boolean existsByUserAndNewsId(User user, String newsId);
}