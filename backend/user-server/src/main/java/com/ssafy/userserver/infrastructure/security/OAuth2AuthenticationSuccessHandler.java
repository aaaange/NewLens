package com.ssafy.userserver.infrastructure.security;

import com.ssafy.userserver.infrastructure.jwt.JwtUtil;
import com.ssafy.userserver.domain.entity.User;
import com.ssafy.userserver.domain.entity.UserProvider;
import com.ssafy.userserver.domain.enums.Active;
import com.ssafy.userserver.domain.enums.ProviderType;
import com.ssafy.userserver.domain.repository.UserProviderRepository;
import com.ssafy.userserver.domain.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final UserRepository userRepository;
	private final UserProviderRepository userProviderRepository;
	private final JwtUtil jwtUtil;

	public OAuth2AuthenticationSuccessHandler(UserRepository userRepository,
		UserProviderRepository userProviderRepository,
		JwtUtil jwtUtil) {
		this.userRepository = userRepository;
		this.userProviderRepository = userProviderRepository;
		this.jwtUtil = jwtUtil;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {
		CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
		String email = oAuth2User.getEmail();
		String nickname = oAuth2User.getNickname();
		String providerId = oAuth2User.getProviderId();
		String registrationId = oAuth2User.getRegistrationId();

		ProviderType providerType = ProviderType.valueOf(registrationId.toUpperCase());

		// 1. 회원 존재 여부 확인 (email 기준)
		User user = userRepository.findByEmail(email).orElseGet(() -> {
			// 신규 회원가입
			User newUser = User.builder()
				.nickname(nickname)
				.email(email)
				.profileImage(null) // 프로필 이미지가 있을 경우 설정
				.active(Active.Y)
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();
			return userRepository.save(newUser);
		});

		// 2. UserProvider 저장 또는 업데이트 (소셜 토큰 정보 포함)
		Optional<UserProvider> optionalUserProvider = userProviderRepository.findByUserIdAndProviderName(user.getId(), providerType);
		UserProvider userProvider = optionalUserProvider.orElseGet(() -> UserProvider.builder()
			.user(user)
			.providerName(providerType)
			.providerId(providerId)
			.accessToken(null)   // 실제 소셜 access token을 넣어줄 수 있음
			.refreshToken(null)  // 실제 소셜 refresh token을 넣어줄 수 있음
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build());
		// 토큰 정보 갱신(예시)
		userProvider = userProviderRepository.save(userProvider);

		// 3. JWT 토큰 생성
		String accessToken = jwtUtil.generateAccessToken(user);
		String refreshToken = jwtUtil.generateRefreshToken(user);

		// 4. Access Token은 헤더에, Refresh Token은 쿠키에 담아서 전달
		response.setHeader("Authorization", "Bearer " + accessToken);

		Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
		refreshCookie.setHttpOnly(true);
		refreshCookie.setPath("/");
		refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
		response.addCookie(refreshCookie);

		// 이후 클라이언트에 JSON 응답을 보내거나 특정 URL로 리다이렉트 할 수 있음.
		response.sendRedirect("/");  // 예시로 홈으로 리다이렉트
	}
}
