package com.ssafy.userserver.domain.dto;

import com.ssafy.userserver.domain.enums.Active;
import com.ssafy.userserver.domain.enums.ProviderName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

	private String email;
	private String nickname;
	private String profileImage;
	private ProviderName providerName;
	private Active active;
}