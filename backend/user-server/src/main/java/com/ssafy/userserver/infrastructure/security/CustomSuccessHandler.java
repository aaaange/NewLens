package com.ssafy.userserver.infrastructure.security;

import com.ssafy.userserver.infrastructure.jwt.JWTUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
@Slf4j
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final JWTUtil jwtUtil;

	public CustomSuccessHandler(JWTUtil jwtUtil) {

		this.jwtUtil = jwtUtil;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException,
		ServletException {

		//OAuth2User
		CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

		String nickname = customUserDetails.getName();
		String email = customUserDetails.getEmail();

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();
		String role = auth.getAuthority();

		String accessToken = jwtUtil.createJwt(email, nickname, 10*60*60*1000L);
		String refreshToken = jwtUtil.createJwt(email, nickname, 14*24*60*60*1000L);

		log.info("배포 확인용 accessToken: {}", accessToken);

//		response.setHeader("Authorization", "Bearer " + accessToken);
//		response.addCookie(createCookie("RefreshToken", refreshToken));
//		response.addCookie(createCookie("AccessToken", accessToken));
//      response.sendRedirect("https://newlens.co.kr/main");
//		response.sendRedirect("http://localhost:5173/main");
		String redirectUrl = "http://localhost:5173/main"
				+ "?accessToken=" + accessToken
				+ "&refreshToken=" + refreshToken;
		response.sendRedirect(redirectUrl);
	}

	private Cookie createCookie(String key, String value) {

		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(14*24*60*60);
		//cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setHttpOnly(true);

		return cookie;
	}
}
