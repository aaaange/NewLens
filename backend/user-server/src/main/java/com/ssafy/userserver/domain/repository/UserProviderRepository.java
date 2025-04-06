package com.ssafy.userserver.domain.repository;

import com.ssafy.userserver.domain.entity.UserProvider;
import com.ssafy.userserver.domain.entity.User;
import com.ssafy.userserver.domain.enums.ProviderName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProviderRepository extends JpaRepository<UserProvider, Long> {
	Optional<UserProvider> findByUserAndProviderName(User user, ProviderName providerName);

	Optional<UserProvider> findByUserIdAndProviderName(Long id, ProviderName providerName);

	Optional<UserProvider> findByUser(User user);
}
