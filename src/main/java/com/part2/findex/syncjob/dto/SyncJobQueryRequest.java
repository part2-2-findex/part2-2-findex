package com.part2.findex.syncjob.dto;

import com.part2.findex.syncjob.entity.SyncJobStatus;
import com.part2.findex.syncjob.entity.SyncJobType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.part2.findex.syncjob.constant.SortDirectionConstant.DEFAULT_SORT_FIELD;
import static com.part2.findex.syncjob.constant.SortDirectionConstant.DESCENDING_SORT_DIRECTION;

public record SyncJobQueryRequest(
        @RequestParam(required = false) SyncJobType jobType,
        @RequestParam(required = false) Long indexInfoId,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @RequestParam(required = false) LocalDate baseDateFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @RequestParam(required = false) LocalDate baseDateTo,
        @RequestParam(required = false) String worker,
        @RequestParam(required = false) LocalDateTime jobTimeFrom,
        @RequestParam(required = false) LocalDateTime jobTimeTo,
        @RequestParam(required = false) SyncJobStatus status,
        @RequestParam(required = false) Long idAfter,
        @RequestParam(required = false) String cursor,
        @RequestParam(required = false) String sortField,
        @RequestParam(required = false) String sortDirection,
        @RequestParam(required = false) Integer size
) {
    private static final String DEFAULT_PAGE_SIZE = "10";

    public SyncJobQueryRequest {
        if (sortField == null || sortField.isBlank()) {
            sortField = DEFAULT_SORT_FIELD;
        }
        if (sortDirection == null || sortDirection.isBlank()) {
            sortDirection = DESCENDING_SORT_DIRECTION;
        }
        if (size == null) {
            size = Integer.valueOf(DEFAULT_PAGE_SIZE);
        }
    }
}
