package com.ssafy.userserver.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

	private String nickname;
	private String email;
	private String profileImage;
}