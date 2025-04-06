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

		String authorization = request.getHeader("Authorization");

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			System.out.println("token null");
			filterChain.doFilter(request, response);
			return;
		}

		String token = authorization.substring(7);

		if (jwtUtil.isExpired(token)) {
			System.out.println("token expired");
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
		Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
		//세션에 사용자 등록
		SecurityContextHolder.getContext().setAuthentication(authToken);

		filterChain.doFilter(request, response);
	}
}


// public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
// 	private final JwtUtil jwtUtil;
//
// 	public JwtAuthenticationFilter(JwtUtil jwtUtil) {
// 		// DI를 사용하거나, Spring Context에서 가져오는 방식으로 변경 가능
// 		this.jwtUtil = jwtUtil;
// 	}
//
// 	@Override
// 	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
// 		throws ServletException, IOException {
// 		String token = getJwtFromRequest(request);
//
// 		if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
// 			Long userId = jwtUtil.getUserIdFromJWT(token);
// 			// 실제 애플리케이션에서는 userId를 기반으로 사용자 상세정보 조회 후 Authentication 객체 생성
// 			UsernamePasswordAuthenticationToken authentication =
// 				new UsernamePasswordAuthenticationToken(userId, null, null);
// 			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
// 			SecurityContextHolder.getContext().setAuthentication(authentication);
// 		}
// 		filterChain.doFilter(request, response);
// 	}
//
// 	private String getJwtFromRequest(HttpServletRequest request) {
// 		String bearerToken = request.getHeader("Authorization");
// 		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
// 			return bearerToken.substring(7);
// 		}
// 		return null;
// 	}
// }
