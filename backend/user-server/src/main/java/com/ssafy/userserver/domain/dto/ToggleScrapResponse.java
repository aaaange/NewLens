package com.ssafy.userserver.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ToggleScrapResponse {
	@JsonProperty("is_scrap")
	private boolean isScrap;
}
