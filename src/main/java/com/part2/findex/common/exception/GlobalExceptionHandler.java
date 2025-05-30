package com.part2.findex.common.exception;


import com.part2.findex.autosync.exception.AutoSyncConfigErrorCode;
import com.part2.findex.autosync.exception.AutoSyncConfigException;
import com.part2.findex.common.dto.ApiErrorStatus;
import com.part2.findex.common.dto.ErrorResponse;
import com.part2.findex.common.mapper.ErrorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
@ResponseBody
public class GlobalExceptionHandler {

    private final ErrorMapper errorMapper;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("잘못된 요청입니다.");

        HttpStatus status = HttpStatus.BAD_REQUEST;

        ApiErrorStatus errorStatus = ApiErrorStatus.fromStatus(status);

        ErrorResponse errorDto = errorMapper.toDto(
                errorStatus.getHttpStatus(),
                errorStatus.getMessage(),
                errorMessage
        );

        return ResponseEntity
                .status(status)
                .body(errorDto);
    }

    @ExceptionHandler(AutoSyncConfigException.class)
    public ResponseEntity<ErrorResponse> handleAutoSyncConfigException(AutoSyncConfigException ex) {
        AutoSyncConfigErrorCode errorCode = ex.getErrorCode();

        HttpStatus status = errorCode.getStatus();

        ApiErrorStatus errorStatus = ApiErrorStatus.fromStatus(status);

        ErrorResponse errorDto = errorMapper.toDto(
                errorStatus.getHttpStatus(),
                errorStatus.getMessage(),
                errorCode.getMessage()
        );

        return ResponseEntity.status(errorCode.getStatus()).body(errorDto);
    }

}
