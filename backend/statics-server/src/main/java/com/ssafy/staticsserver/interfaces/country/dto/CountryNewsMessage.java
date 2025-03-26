package com.ssafy.staticsserver.interfaces.country.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CountryNewsMessage {
    private String keyword;
    private String keywordMind;
    private String country1;
    private String country2;
    private List<String> country1NewsIds;
    private List<String> country2NewsIds;
}
