package com.part2.findex.syncjob.dto;

import com.part2.findex.syncjob.constant.SortDirectionConstant;
import com.part2.findex.syncjob.constant.SortField;
import com.part2.findex.syncjob.constant.SyncJobStatus;
import com.part2.findex.syncjob.constant.SyncJobType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
        @RequestParam(required = false, defaultValue = SortField.DEFAULT_SORT_FIELD) String sortField,
        @RequestParam(required = false, defaultValue = SortDirectionConstant.DESCENDING_SORT_DIRECTION) String sortDirection,
        @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer size
) {
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_FIELD = "createdAt";
    public static final String DEFAULT_SORT_DIRECTION = "DESC";

    public SyncJobQueryRequest {
        if (sortField == null || sortField.isBlank()) {
            sortField = DEFAULT_SORT_FIELD;
        }
        if (sortDirection == null || sortDirection.isBlank()) {
            sortDirection = DEFAULT_SORT_DIRECTION;
        }
        if (size == null) {
            size = Integer.valueOf(DEFAULT_PAGE_SIZE);
        }
    }
}
