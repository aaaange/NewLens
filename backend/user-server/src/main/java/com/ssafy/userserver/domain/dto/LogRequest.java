package com.ssafy.userserver.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogRequest {
	@JsonProperty("news_id")
	private String newsId;
}
