// package com.ssafy.userserver.application.auth;
//
// import com.ssafy.userserver.domain.entity.User;
// import com.ssafy.userserver.domain.entity.UserProvider;
// import com.ssafy.userserver.domain.enums.Active;
// import com.ssafy.userserver.domain.enums.ProviderType;
// import com.ssafy.userserver.domain.repository.UserProviderRepository;
// import com.ssafy.userserver.domain.repository.UserRepository;
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;
//
// import java.time.LocalDateTime;
// import java.util.Map;
// import java.util.Optional;
//
// @Service
// @RequiredArgsConstructor
// public class AuthService {
//
// 	private final UserRepository userRepository;
// 	private final UserProviderRepository userProviderRepository;
//
// 	/**
// 	 * 소셜 로그인으로 전달받은 정보를 기반으로 User 및 UserProvider를 저장 또는 갱신한다.
// 	 *
// 	 * @param providerType      소셜 공급자 (예: GOOGLE, KAKAO)
// 	 * @param accessToken       소셜 공급자로부터 받은 access token
// 	 * @param refreshToken      소셜 공급자로부터 받은 refresh token (없을 수도 있음)
// 	 * @param attributes        소셜 공급자가 전달한 사용자 정보
// 	 * @return 저장된 User 엔티티
// 	 */
// 	public User processOAuth2User(ProviderType providerType, String accessToken, String refreshToken, Map<String, Object> attributes) {
// 		String email = null;
// 		String nickname = null;
// 		String profileImage = null;
// 		String providerId = null;
//
// 		if (providerType == ProviderType.GOOGLE) {
// 			email = (String) attributes.get("email");
// 			nickname = (String) attributes.get("name");
// 			profileImage = (String) attributes.get("picture");
// 			// Google의 경우 보통 "sub" 필드가 사용자 ID 역할
// 			providerId = (String) attributes.get("sub");
// 		} else if (providerType == ProviderType.KAKAO) {
// 			Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
// 			if (kakaoAccount != null) {
// 				email = (String) kakaoAccount.get("email");
// 				Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
// 				if (profile != null) {
// 					nickname = (String) profile.get("nickname");
// 					profileImage = (String) profile.get("profile_image_url");
// 				}
// 			}
// 			// Kakao의 경우 root attributes에 "id" 필드가 사용자 ID
// 			providerId = String.valueOf(attributes.get("id"));
// 		}
//
// 		LocalDateTime now = LocalDateTime.now();
// 		Optional<User> userOpt = userRepository.findByEmail(email);
// 		User user;
// 		if (userOpt.isPresent()) {
// 			user = userOpt.get();
// 			// 기존 사용자 정보 업데이트 (필요에 따라 조건부 업데이트)
// 			user.setNickname(nickname);
// 			user.setProfileImage(profileImage);
// 			user.setUpdatedAt(now);
// 			user = userRepository.save(user);
// 		} else {
// 			user = User.builder()
// 				.nickname(nickname)
// 				.email(email)
// 				.profileImage(profileImage)
// 				.active(Active.ACTIVE)  // 예: Active.ACTIVE
// 				.createdAt(now)
// 				.updatedAt(now)
// 				.build();
// 			user = userRepository.save(user);
// 		}
//
// 		// UserProvider 처리: 해당 User와 공급자 조합으로 저장/업데이트
// 		Optional<UserProvider> userProviderOpt = userProviderRepository.findByUserAndProviderName(user, providerType);
// 		UserProvider userProvider;
// 		if (userProviderOpt.isPresent()) {
// 			userProvider = userProviderOpt.get();
// 			userProvider.setAccessToken(accessToken);
// 			userProvider.setRefreshToken(refreshToken);
// 			userProvider.setUpdatedAt(now);
// 			userProviderRepository.save(userProvider);
// 		} else {
// 			userProvider = UserProvider.builder()
// 				.user(user)
// 				.providerName(providerType)
// 				.providerId(providerId)
// 				.accessToken(accessToken)
// 				.refreshToken(refreshToken)
// 				.createdAt(now)
// 				.updatedAt(now)
// 				.build();
// 			userProviderRepository.save(userProvider);
// 		}
//
// 		return user;
// 	}
// }
