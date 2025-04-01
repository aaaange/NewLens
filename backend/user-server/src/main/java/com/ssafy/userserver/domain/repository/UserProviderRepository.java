package com.ssafy.userserver.domain.repository;

import com.ssafy.userserver.domain.entity.UserProvider;
import com.ssafy.userserver.domain.entity.User;
import com.ssafy.userserver.domain.enums.ProviderType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProviderRepository extends JpaRepository<UserProvider, Long> {
	Optional<UserProvider> findByUserAndProviderName(User user, ProviderType providerName);

	Optional<UserProvider> findByUserIdAndProviderName(Long id, ProviderType providerType);
}
