package com.ssafy.staticsserver.domain.user.repository;

import com.ssafy.staticsserver.domain.user.entity.Scrap;
import com.ssafy.staticsserver.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    List<Scrap> findByUserAndNewsIdIn(User user, List<String> newsIds);
}

