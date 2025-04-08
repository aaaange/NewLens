package com.ssafy.userserver.domain.dto;

import java.util.Map;

import com.ssafy.userserver.domain.enums.ProviderName;

public class SsafyResponse implements OAuth2Response{

	private final Map<String, Object> attribute;

	public SsafyResponse(Map<String, Object> attribute) {

		this.attribute = attribute;
	}

	@Override
	public String getProviderName() {
		return ProviderName.SSAFY.name();
	}

	@Override
	public String getProviderId() {
		return attribute.get("userId").toString();
	}

	@Override
	public String getEmail() {
		return attribute.get("email").toString();
	}

	@Override
	public String getNickname() {
		return attribute.get("name").toString();
	}

	@Override
	public String getProfileImageUrl() {
		return "";
	}
}
