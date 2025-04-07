package com.ssafy.userserver.infrastructure.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import com.ssafy.userserver.domain.dto.UserDTO;
import com.ssafy.userserver.infrastructure.security.CustomOAuth2User;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

	private final JWTUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		// AccessToken 추출
		String authorization = request.getHeader("Authorization");

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			System.out.println("token null");
			filterChain.doFilter(request, response);
			return;
		}

		String token = authorization.substring(7);

		// AccessToken 만료 여부 확인
		if (jwtUtil.isExpired(token)) {
			System.out.println("token expired");

			String refreshToken = jwtUtil.extractRefreshToken(request);

			// refresh token이 존재하며 유효한 경우
			if (refreshToken != null && !jwtUtil.isExpired(refreshToken)) {
				String email = jwtUtil.getEmail(refreshToken);
				String nickname = jwtUtil.getNickname(refreshToken);

				String newAccessToken = jwtUtil.createJwt(email, nickname, 10*60*60*1000L);
				response.setHeader("Authorization", "Bearer " + newAccessToken);
				System.out.println("New access token generated");

				// SecurityContext에 사용자 정보 등록
				UserDTO userDTO = new UserDTO();
				userDTO.setEmail(email);
				userDTO.setNickname(nickname);
				CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);
				Authentication authToken = new UsernamePasswordAuthenticationToken(
					customOAuth2User, null, customOAuth2User.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(authToken);
			} else {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Invalid refresh token");
			}

			filterChain.doFilter(request, response);
			return;
		}

		String email = jwtUtil.getEmail(token);
		String nickname = jwtUtil.getNickname(token);

		UserDTO userDTO = new UserDTO();
		userDTO.setNickname(nickname);
		userDTO.setEmail(email);

		//UserDetails에 회원 정보 객체 담기
		CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);
		//스프링 시큐리티 인증 토큰 생성
		Authentication authToken = new UsernamePasswordAuthenticationToken( customOAuth2User, null, customOAuth2User.getAuthorities());
		//세션에 사용자 등록
		SecurityContextHolder.getContext().setAuthentication(authToken);

		filterChain.doFilter(request, response);
	}
}