package com.ssafy.userserver.infrastructure.config;

import java.util.Arrays;
import java.util.Collections;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.ssafy.userserver.infrastructure.jwt.JWTFilter;
import com.ssafy.userserver.infrastructure.jwt.JWTUtil;
import com.ssafy.userserver.infrastructure.security.CustomOAuth2UserService;
import com.ssafy.userserver.infrastructure.security.CustomSuccessHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final CustomOAuth2UserService customOAuth2UserService;
	private final CustomSuccessHandler customSuccessHandler;
	private final JWTUtil jwtUtil;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http
				.cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

					@Override
					public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

						CorsConfiguration configuration = new CorsConfiguration();

						configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "https://newlens.co.kr","https://www.newlens.co.kr"));
						configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
						configuration.setAllowCredentials(false);
						configuration.setAllowedHeaders(Collections.singletonList("*"));
						configuration.setMaxAge(3600L);

						configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));


						return configuration;
					}
				}));

		//csrf disable
		http
				.csrf((auth) -> auth.disable());

		//From 로그인 방식 disable
		http
				.formLogin((auth) -> auth.disable());

		//HTTP Basic 인증 방식 disable
		http
				.httpBasic((auth) -> auth.disable());

		//JWTFilter 추가
		http
				.addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

		//oauth2
		http
				.oauth2Login((oauth2) -> oauth2
						.redirectionEndpoint(redirection -> redirection
							.baseUri("/api/user/login/oauth2/code/*")
						)
						.userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
								.userService(customOAuth2UserService))
						.successHandler(customSuccessHandler)
				);

		//경로별 인가 작업
		http
				.authorizeHttpRequests((auth) -> auth
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						.requestMatchers("/test1").permitAll()
						// .requestMatchers("my").hasRole("USER")
						.anyRequest().authenticated());

		//세션 설정 : STATELESS
		http
				.sessionManagement((session) -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		return http.build();
	}
}
