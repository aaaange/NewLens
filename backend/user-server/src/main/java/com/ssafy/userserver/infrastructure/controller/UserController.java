package com.ssafy.userserver.infrastructure.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.userserver.common.dto.CommonResponse;
import com.ssafy.userserver.domain.dto.UserDTO;
import com.ssafy.userserver.infrastructure.security.CustomOAuth2User;
import com.ssafy.userserver.infrastructure.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user/auth")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	// 로그아웃
	@PostMapping("/logout")
	public ResponseEntity<CommonResponse<String>> logout(HttpServletResponse response) {

		CommonResponse<String> data = userService.logout(response);
		return ResponseEntity.ok(data);
	}

	// 회원 탈퇴
	@PostMapping("/signout")
	public ResponseEntity<CommonResponse<String>> signout(@AuthenticationPrincipal CustomOAuth2User user) {
		CommonResponse<String> data = userService.signout(user);
		return ResponseEntity.ok(data);
	}

	// 회원 정보 조회
	@GetMapping("/user_info")
	public ResponseEntity<CommonResponse<UserDTO>> getCurrentUser(@AuthenticationPrincipal CustomOAuth2User user) {
		CommonResponse<UserDTO> data = userService.getCurrentUser(user);
		return ResponseEntity.ok(data);
	}



}
