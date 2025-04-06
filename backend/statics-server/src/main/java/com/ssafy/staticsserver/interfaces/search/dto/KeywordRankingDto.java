package com.ssafy.staticsserver.interfaces.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KeywordRankingDto {
	private String name;
	private int count;
	private String state;
}
