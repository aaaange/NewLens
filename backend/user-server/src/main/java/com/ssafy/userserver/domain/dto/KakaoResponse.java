package com.ssafy.userserver.domain.dto;

import java.util.Map;

import com.ssafy.userserver.domain.enums.ProviderName;

public class KakaoResponse implements OAuth2Response {

	private final Map<String, Object> attribute;

	public KakaoResponse(Map<String, Object> attribute) {

		this.attribute = attribute;
	}

	@Override
	public String getProviderName() {
		return ProviderName.KAKAO.name();
	}

	@Override
	public String getProviderId() {
		return attribute.get("id").toString();
	}

	@Override
	public String getEmail() {
		Object kakaoAccountObj = attribute.get("kakao_account");
		if (kakaoAccountObj instanceof Map) {
			Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoAccountObj;
			return (String) kakaoAccount.get("email");
		}
		return null;
	}

	@Override
	public String getNickname() {
		Object kakaoAccountObj = attribute.get("kakao_account");
		if (kakaoAccountObj instanceof Map) {
			Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoAccountObj;
			Object profileObj = kakaoAccount.get("profile");
			if (profileObj instanceof Map) {
				Map<String, Object> profile = (Map<String, Object>) profileObj;
				return (String) profile.get("nickname");
			}
		}
		return null;
	}

	@Override
	public String getProfileImageUrl() {
		Object kakaoAccountObj = attribute.get("kakao_account");
		if (kakaoAccountObj instanceof Map) {
			Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoAccountObj;
			Object profileObj = kakaoAccount.get("profile");
			if (profileObj instanceof Map) {
				Map<String, Object> profile = (Map<String, Object>) profileObj;
				return (String) profile.get("profile_image_url");
			}
		}
		return null;
	}
}
