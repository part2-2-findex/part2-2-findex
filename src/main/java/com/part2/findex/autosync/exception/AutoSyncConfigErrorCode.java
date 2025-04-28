package com.part2.findex.autosync.exception;


import com.part2.findex.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum AutoSyncConfigErrorCode implements ErrorCode {
    CONFIG_NOT_FOUND("자동 연동 설정을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus status;

    AutoSyncConfigErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}