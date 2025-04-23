package com.part2.findex.common.mapper;

import com.part2.findex.common.dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class ErrorMapper {
    public ErrorDto toDto(HttpStatus status, String message, String details) {
        return ErrorDto.builder()
                .timestamp(ZonedDateTime.now())
                .status(status)
                .message(message)
                .details(details)
                .build();
    }
}
