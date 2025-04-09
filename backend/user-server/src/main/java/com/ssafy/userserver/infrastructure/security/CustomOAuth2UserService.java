package com.ssafy.userserver.infrastructure.security;

import com.ssafy.userserver.domain.dto.GoogleResponse;
import com.ssafy.userserver.domain.dto.KakaoResponse;
import com.ssafy.userserver.domain.dto.OAuth2Response;
import com.ssafy.userserver.domain.dto.SsafyResponse;
import com.ssafy.userserver.domain.dto.UserDTO;
import com.ssafy.userserver.domain.entity.User;
import com.ssafy.userserver.domain.entity.UserProvider;
import com.ssafy.userserver.domain.enums.Active;
import com.ssafy.userserver.domain.enums.ProviderName;
import com.ssafy.userserver.domain.repository.UserProviderRepository;
import com.ssafy.userserver.domain.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final UserRepository userRepository;
	private final UserProviderRepository userProviderRepository;

	@Override
	@Transactional
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		OAuth2User oAuth2User = super.loadUser(userRequest);

		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		OAuth2Response oAuth2Response = null;
		if (registrationId.equals("kakao")) {
			oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
		}
		else if (registrationId.equals("google")) {
			oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
		}
		else if (registrationId.equals("ssafy")) {
			oAuth2Response = new SsafyResponse(oAuth2User.getAttributes());
		}
		else {
			return null;
		}

		String email = oAuth2Response.getEmail();
		User existUser = userRepository.findByEmail(email);

		// 기존 회원 정보 없으면 DB에 추가
		if (existUser == null) {
			User newUser = User.builder()
				.email(email)
				.nickname(oAuth2Response.getNickname())
				.profileImage(oAuth2Response.getProfileImageUrl())
				.active(Active.Y)
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();
			userRepository.save(newUser);

			Object refreshTokenObj = userRequest.getAdditionalParameters().get("refresh_token");
			String refreshToken = refreshTokenObj != null ? refreshTokenObj.toString() : null;

			UserProvider newUserProvider = UserProvider.builder()
				.user(newUser)
				.providerName(ProviderName.valueOf(oAuth2Response.getProviderName()))
				.providerId(oAuth2Response.getProviderId())
				.accessToken(userRequest.getAccessToken().getTokenValue())
				.refreshToken(refreshToken)
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();
			userProviderRepository.save(newUserProvider);

			UserDTO userDTO = new UserDTO();
			userDTO.setEmail(email);
			userDTO.setNickname(oAuth2Response.getNickname());
			userDTO.setProfileImage(oAuth2Response.getProfileImageUrl());
			userDTO.setProviderName(ProviderName.valueOf(oAuth2Response.getProviderName()));
			userDTO.setActive(Active.Y);

			return new CustomOAuth2User(userDTO);
		}
		// 이미 회원가입 되어 있는 경우
		else {
			UserProvider existUserProvider = userProviderRepository.findByUser(existUser)
				.orElseThrow(() -> new NoSuchElementException("조회되는 정보가 없습니다."));

			// 이미 다른 provider로 회원가입한 경우
			if (!existUserProvider.getProviderName().toString()
				.equalsIgnoreCase(oAuth2Response.getProviderName())) {
				throw new OAuth2AuthenticationException(
					new OAuth2Error("provider_mismatch",
						existUserProvider.getProviderName().toString(), null)
				);
			}

			UserDTO userDTO = new UserDTO();
			userDTO.setEmail(email);
			userDTO.setNickname(oAuth2Response.getNickname());
			userDTO.setProfileImage(oAuth2Response.getProfileImageUrl());
			userDTO.setProviderName(ProviderName.valueOf(oAuth2Response.getProviderName()));
			userDTO.setActive(Active.Y);

			return new CustomOAuth2User(userDTO);
		}
	}

	// @Override
	// public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
	// 	OAuth2User oAuth2User = super.loadUser(userRequest);
	// 	// provider 구분 (예: google, kakao)
	// 	String registrationId = userRequest.getClientRegistration().getRegistrationId();
	//
	// 	// provider 별로 attribute key가 다를 수 있음.
	// 	Map<String, Object> attributes = oAuth2User.getAttributes();
	// 	String email = null;
	// 	String nickname = null;
	// 	String profileImage = null;
	// 	String providerId = null;
	//
	// 	if ("google".equals(registrationId)) {
	// 		// Google에서 받은 사용자 정보 구조
	// 		// attributes :
	// 		// {
	// 		//   "sub": "google12345",
	// 		//   "name": "John Doe",
	// 		//   "email": "john.doe@example.com"
	// 		// }
	// 		email = (String) attributes.get("email");
	// 		nickname = (String) attributes.get("name");
	// 		profileImage = (String) attributes.get("picture");
	// 		providerId = (String) attributes.get("sub");
	// 	} else if ("kakao".equals(registrationId)) {
	// 		// Kakao에서 받은 사용자 정보 구조
	// 		// attributes :
	// 		// {
	// 		//   "id": 98765,
	// 		//   "kakao_account": {
	// 		//       "email": "jane.doe@example.com",
	// 		//       "profile": {
	// 		//           "nickname": "Jane"
	// 		//       }
	// 		//   }
	// 		// }
	// 		Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
	// 		email = (String) kakaoAccount.get("email");
	// 		Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
	// 		nickname = (String) profile.get("nickname");
	// 		profileImage = (String) profile.get("profile_image");
	// 		providerId = String.valueOf(attributes.get("id"));
	// 	}
	// 	// 이후 회원가입 또는 업데이트 로직을 구현할 수 있음.
	// 	// 예를 들어, email을 기준으로 회원이 존재하는지 확인 후, 없으면 신규 등록, 있으면 토큰 갱신 등.
	// 	// 본 로직은 OAuth2AuthenticationSuccessHandler에서도 처리할 수 있음.
	//
	// 	// 필요에 따라 커스텀 OAuth2User 객체에 추가 정보를 담아 반환할 수 있음.
	// 	return new CustomOAuth2User(oAuth2User.getAuthorities(), attributes, "email", email, nickname, profileImage, providerId, registrationId);
	// }
}
