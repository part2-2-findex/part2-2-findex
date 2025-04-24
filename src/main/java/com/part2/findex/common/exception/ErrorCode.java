package com.part2.findex.common.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    String getMessage();
    HttpStatus getStatus();
}
