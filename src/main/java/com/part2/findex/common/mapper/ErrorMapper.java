package com.part2.findex.common.mapper;

import com.part2.findex.common.dto.ErrorResponse;
import com.part2.findex.indexinfo.dto.response.IndexInfoDto;
import com.part2.findex.indexinfo.entity.IndexInfo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class ErrorMapper {
    public ErrorResponse toDto(HttpStatus status, String message, String details) {
        return ErrorResponse.builder()
                .timestamp(ZonedDateTime.now())
                .status(status)
                .message(message)
                .details(details)
                .build();
    }
}
