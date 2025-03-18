package com.ssafy.searchserver.common;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommonResponse {
    private String code;
    private boolean success;
    private String message;
}
