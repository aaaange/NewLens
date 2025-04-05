package com.ssafy.userserver.infrastructure.service;

import org.springframework.stereotype.Service;

import com.ssafy.userserver.common.dto.CommonResponse;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	// 로그아웃 (쿠키의 refreshToken 삭제)
	public CommonResponse<String> logout(HttpServletResponse response) {
		Cookie cookie = new Cookie("RefreshToken", null);
		cookie.setMaxAge(0);
		cookie.setPath("/");
		response.addCookie(cookie);
		return CommonResponse.success(null);
	}
}
