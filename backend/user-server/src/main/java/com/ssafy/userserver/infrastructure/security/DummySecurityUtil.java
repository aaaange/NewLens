package com.ssafy.userserver.infrastructure.security;

import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 개발 환경 전용 더미 인증 유틸리티
 * 실제 운영 환경에서는 사용하지 않도록 @Profile("dev")로 제한합니다.
 */
@Profile("dev")
public class DummySecurityUtil {

	// 단순히 사용자 ID만 포함하는 DTO (실제 필요한 정보가 더 있으면 확장 가능)
	public static class OAuth2UserDTO {
		private String id;

		public OAuth2UserDTO(String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	/**
	 * SecurityContext에 임시(dummy) 인증 정보를 설정합니다.
	 * 개발 및 테스트 환경에서 추천 로직 등을 테스트할 때 호출합니다.
	 */
	public static void setDummyAuthenticationIfNotExists() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			// ROLE_USER 권한 부여
			List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

			// 임의의 ID("100")를 가진 더미 사용자 생성
			OAuth2UserDTO dummyUser = new OAuth2UserDTO("100");

			// 더미 인증 객체 생성 후 SecurityContext에 설정
			Authentication dummyAuth = new UsernamePasswordAuthenticationToken(dummyUser, null, authorities);
			SecurityContextHolder.getContext().setAuthentication(dummyAuth);
		}
	}
}