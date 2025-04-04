package com.ssafy.userserver.domain.dto;

import java.util.Map;

import com.ssafy.userserver.domain.enums.ProviderType;

public class GoogleResponse implements OAuth2Response{

	private final Map<String, Object> attribute;

	public GoogleResponse(Map<String, Object> attribute) {

		this.attribute = attribute;
	}

	@Override
	public String getProvider() {
		return ProviderType.GOOGLE.name();
	}

	@Override
	public String getProviderId() {
		return attribute.get("sub").toString();
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
		return attribute.get("picture").toString();
	}
}

