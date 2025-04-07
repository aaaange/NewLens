package com.ssafy.userserver.infrastructure.config;

import java.util.List;

import com.ssafy.userserver.infrastructure.jwt.JWTFilter;
import com.ssafy.userserver.infrastructure.jwt.JWTUtil;
import com.ssafy.userserver.infrastructure.security.CustomOAuth2UserService;
import com.ssafy.userserver.infrastructure.security.CustomSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

	private final CustomOAuth2UserService customOAuth2UserService;
	private final CustomSuccessHandler customSuccessHandler;
	private final JWTUtil jwtUtil;

	// 🔐 보안 필터 체인 설정
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http
			.cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 적용
			.csrf(csrf -> csrf.disable()) // CSRF 비활성화
			.formLogin(form -> form.disable()) // 기본 로그인폼 비활성화
			.httpBasic(basic -> basic.disable()) // HTTP Basic 인증 비활성화
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 비활성화
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // OPTIONS 요청 허용 (Preflight)
				.requestMatchers("/test1").permitAll() // 테스트용 API 허용
				.anyRequest().authenticated()) // 나머지 요청은 인증 필요
			.oauth2Login(oauth2 -> oauth2
				.userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
				.successHandler(customSuccessHandler)) // OAuth2 설정
			.addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class); // JWT 필터 등록

		return http.build();
	}

	// ✅ 전역 CORS 설정
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();

		config.setAllowCredentials(true); // 쿠키, 인증정보 허용
		config.setAllowedOrigins(List.of("https://www.newlens.co.kr", "http://localhost:3000")); // 허용할 프론트엔드 주소
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("*")); // 모든 요청 헤더 허용
		config.setExposedHeaders(List.of("Authorization", "Set-Cookie")); // 응답 헤더 노출
		config.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	// ☑️ 선택: CorsFilter 별도로 등록해도 확실하게 적용됨
	@Bean
	public CorsFilter corsFilter() {
		return new CorsFilter(corsConfigurationSource());
	}
}
