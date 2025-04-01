// package com.ssafy.userserver.domain.dto;
//
// import java.time.LocalDateTime;
// import java.util.Map;
//
// import com.ssafy.userserver.domain.entity.User;
// import com.ssafy.userserver.domain.enums.Active;
//
// import jakarta.security.auth.message.AuthException;
// import lombok.Builder;
//
// @Builder
// public record OAuth2UserInfo(
// 	String name,
// 	String email,
// 	String profile
// ) {
//
// 	public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) {
// 		return switch (registrationId) { // registration id별로 userInfo 생성
// 			case "google" -> ofGoogle(attributes);
// 			case "kakao" -> ofKakao(attributes);
// 			default -> throw new AuthException(ILLEGAL_REGISTRATION_ID);
// 		};
// 	}
//
// 	private static OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {
// 		return OAuth2UserInfo.builder()
// 			.name((String) attributes.get("name"))
// 			.email((String) attributes.get("email"))
// 			.profile((String) attributes.get("picture"))
// 			.build();
// 	}
//
// 	private static OAuth2UserInfo ofKakao(Map<String, Object> attributes) {
// 		Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
// 		Map<String, Object> profile = (Map<String, Object>) account.get("profile");
//
// 		return OAuth2UserInfo.builder()
// 			.name((String) profile.get("nickname"))
// 			.email((String) account.get("email"))
// 			.profile((String) profile.get("profile_image_url"))
// 			.build();
// 	}
//
// 	public User toEntity() {
// 		return User.builder()
// 			.nickname(name) // 현재 DTO의 name 필드를 바로 사용
// 			.email(email)
// 			.profileImage(profile) // User 엔티티에 맞춰 profileImage로 변경
// 			.active(Active.Y) // 엔티티 정의에 맞게 수정
// 			.createdAt(LocalDateTime.now())
// 			.updatedAt(LocalDateTime.now())
// 			.build();
// 	}
// }
