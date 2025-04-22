package com.part2.findex.common.dto;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

public enum ApiErrorStatus {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근이 거부되었습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다."),
    CONFLICT(HttpStatus.CONFLICT, "중복된 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ApiErrorStatus(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public static ApiErrorStatus fromStatus(HttpStatus status) {
        for (ApiErrorStatus errorStatus : values()) {
            if (errorStatus.httpStatus == status) {
                return errorStatus;
            }
        }
        return INTERNAL_SERVER_ERROR; // 기본값
    }
}
