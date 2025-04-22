package com.part2.findex.common.exception;


import com.part2.findex.common.dto.ApiErrorStatus;
import com.part2.findex.common.dto.ErrorDto;
import com.part2.findex.common.mapper.ErrorMapper;
import io.swagger.v3.core.model.ApiDescription;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.List;
import java.util.stream.Collectors;


@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorMapper errorMapper;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("잘못된 요청입니다.");

        HttpStatus status = HttpStatus.BAD_REQUEST;

        ApiErrorStatus errorStatus = ApiErrorStatus.fromStatus(HttpStatus.BAD_REQUEST);

        ErrorDto errorDto = errorMapper.toDto(
                errorStatus.getHttpStatus(),
                errorStatus.getMessage(),
                errorMessage
        );

        return ResponseEntity
                .status(status)
                .body(errorDto);
    }

}
