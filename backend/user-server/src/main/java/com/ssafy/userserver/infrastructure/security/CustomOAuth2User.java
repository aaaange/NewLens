package com.ssafy.userserver.infrastructure.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.ssafy.userserver.domain.dto.UserDTO;

public class CustomOAuth2User implements OAuth2User {

	private final UserDTO userDTO;

	public CustomOAuth2User(UserDTO userDTO) {

		this.userDTO = userDTO;
	}

	@Override
	public Map<String, Object> getAttributes() {

		return null;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		Collection<GrantedAuthority> collection = new ArrayList<>();

		collection.add(new GrantedAuthority() {

			@Override
			public String getAuthority() {

				return null;
			}
		});

		return collection;
	}

	@Override
	public String getName() {
		return userDTO.getNickname();
	}

	public String getEmail() {
		return userDTO.getEmail();
	}
}

// public class CustomOAuth2User implements OAuth2User {
//
// 	private Collection<? extends GrantedAuthority> authorities;
// 	private Map<String, Object> attributes;
// 	private String nameAttributeKey;
// 	private String email;
// 	private String nickname;
// 	private String profileImage;
// 	private String providerId;
// 	private String registrationId;
//
// 	public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
// 		Map<String, Object> attributes,
// 		String nameAttributeKey,
// 		String email,
// 		String nickname,
// 		String profileImage,
// 		String providerId,
// 		String registrationId) {
// 		this.authorities = authorities;
// 		this.attributes = attributes;
// 		this.nameAttributeKey = nameAttributeKey;
// 		this.email = email;
// 		this.nickname = nickname;
// 		this.profileImage = profileImage;
// 		this.providerId = providerId;
// 		this.registrationId = registrationId;
// 	}
//
// 	@Override
// 	public Map<String, Object> getAttributes() {
// 		return attributes;
// 	}
//
// 	@Override
// 	public Collection<? extends GrantedAuthority> getAuthorities() {
// 		return authorities;
// 	}
//
// 	@Override
// 	public String getName() {
// 		return (String) attributes.get(nameAttributeKey);
// 	}
//
// 	public String getEmail() {
// 		return email;
// 	}
//
// 	public String getNickname() {
// 		return nickname;
// 	}
//
// 	public String getProfileImage() {
// 		return profileImage;
// 	}
//
// 	public String getProviderId() {
// 		return providerId;
// 	}
//
// 	public String getRegistrationId() {
// 		return registrationId;
// 	}
// }
