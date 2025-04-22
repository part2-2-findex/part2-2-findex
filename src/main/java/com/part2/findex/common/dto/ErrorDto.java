package com.part2.findex.common.dto;

import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Builder
public record ErrorDto(
    ZonedDateTime timestamp,
    HttpStatus status,
    String message,
    String details
) {
}
