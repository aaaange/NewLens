package com.ssafy.userserver.infrastructure.service;

import java.util.NoSuchElementException;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import com.ssafy.userserver.common.dto.CommonResponse;
import com.ssafy.userserver.domain.dto.UserDTO;
import com.ssafy.userserver.domain.entity.User;
import com.ssafy.userserver.domain.entity.UserProvider;
import com.ssafy.userserver.domain.enums.Active;
import com.ssafy.userserver.domain.repository.UserProviderRepository;
import com.ssafy.userserver.domain.repository.UserRepository;
import com.ssafy.userserver.infrastructure.security.CustomOAuth2User;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final UserProviderRepository userProviderRepository;

	// 로그아웃 (쿠키의 refreshToken 삭제)
	public CommonResponse<String> logout(HttpServletResponse response) {
		Cookie cookie = new Cookie("RefreshToken", null);
		cookie.setMaxAge(0);
		cookie.setPath("/");
		response.addCookie(cookie);
		return CommonResponse.success(null);
	}

	// 회원 탈퇴
	public CommonResponse<String> signout(@AuthenticationPrincipal CustomOAuth2User user) {
		User existUser = userRepository.findByEmailAndActive(user.getEmail(), Active.Y)
			.orElseThrow(() -> new NoSuchElementException("조회된 사용자가 없습니다."));

		existUser.changeActivate(existUser.getActive());
		userRepository.save(existUser);

		return CommonResponse.success(null);
	}

	// 회원 정보 조회
	public CommonResponse<UserDTO> getCurrentUser(CustomOAuth2User user) {
		// User 엔티티 조회 (email, nickname, profileImage, active)
		User userEntity = userRepository.findByEmailAndActive(user.getEmail(), Active.Y)
			.orElseThrow(() -> new NoSuchElementException("사용자 정보를 찾을 수 없습니다."));

		// UserProvider 엔티티에서 provider 정보를 조회 (providerName)
		UserProvider userProvider = userProviderRepository.findByUser(userEntity)
			.orElseThrow(() -> new NoSuchElementException("사용자 provider 정보를 찾을 수 없습니다."));

		// DTO에 값 설정
		UserDTO dto = new UserDTO();
		dto.setEmail(userEntity.getEmail());
		dto.setNickname(userEntity.getNickname());
		dto.setProfileImage(userEntity.getProfileImage());
		dto.setActive(userEntity.getActive());
		dto.setProviderName(userProvider.getProviderName());

		return CommonResponse.success(dto);
	}
}
