package com.ssafy.searchserver.common.exeception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 공통
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "INVALID_PARAMETER", "필수 파라미터가 누락ㅋ"),
    INVALID_METHOD(HttpStatus.METHOD_NOT_ALLOWED, "INVALID_METHOD", "GET POST 구분 합시다잉"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 오류인데 내탓 아님ㅎ"),
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "INVALID_CATEGORY", "그런 카테고리 없음ㅋ"),
    INVALID_PERIOD(HttpStatus.BAD_REQUEST, "INVALID_PERIOD", "기간 제대로ㅋ"),
    INVALID_COUNTRY(HttpStatus.BAD_REQUEST, "INVALID_COUNTRY", "그건 G20 아님ㅋ"),
    INVALID_PAGE(HttpStatus.BAD_REQUEST, "INVALID_PAGE", "유효하지 않은 페이지ㅋ");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
