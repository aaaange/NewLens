package com.ssafy.userserver.domain.repository;

import com.ssafy.userserver.domain.entity.User;
import com.ssafy.userserver.domain.enums.Active;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByEmail(String email);

	Optional<User> findByEmailAndActive(String email, Active active);
}
