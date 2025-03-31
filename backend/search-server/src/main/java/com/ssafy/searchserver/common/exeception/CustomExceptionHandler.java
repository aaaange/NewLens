package com.ssafy.searchserver.common.exeception;

import com.ssafy.searchserver.common.dto.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CommonResponse<Object>> handleCustomException(CustomException ex) {
        ErrorCode error = ex.getErrorCode();
        return ResponseEntity
                .status(error.getStatus())
                .body(CommonResponse.fail(error.getCode(),error.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<CommonResponse<Object>> handleMissingParams(MissingServletRequestParameterException ex) {
        return ResponseEntity
                .status(ErrorCode.INVALID_PARAMETER.getStatus())
                .body(CommonResponse.fail(
                        ErrorCode.INVALID_PARAMETER.getCode(),
                        "필수 파라미터 " + ex.getParameterName() + " 누락이요ㅋ"
                ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CommonResponse<Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String name = ex.getName();
        String message = "요청 파라미터 " + name + " 타입 맞춰요ㅋ";
        return ResponseEntity
                .status(ErrorCode.INVALID_PARAMETER.getStatus())
                .body(CommonResponse.fail(ErrorCode.INVALID_PARAMETER.getCode(), message));
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<CommonResponse<Object>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        String name = ex.getMethod();
        String message = "HTTP 메서드 " + name + " 타입 아닙니다ㅋ";
        return ResponseEntity
                .status(ErrorCode.INVALID_METHOD.getStatus())
                .body(CommonResponse.fail(ErrorCode.INVALID_METHOD.getCode(), message));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Object>> handleAll(Exception ex) {
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(CommonResponse.fail(
                        ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                        ErrorCode.INTERNAL_SERVER_ERROR.getMessage()
                ));
    }
}