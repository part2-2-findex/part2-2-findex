package com.part2.findex.autosyncconfig.exception;

import lombok.Getter;

@Getter
public class AutoSyncConfigException extends RuntimeException {
    private final AutoSyncConfigErrorCode errorCode;

    public AutoSyncConfigException(AutoSyncConfigErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}