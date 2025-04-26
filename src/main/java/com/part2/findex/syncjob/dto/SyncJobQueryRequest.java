package com.part2.findex.syncjob.dto;

import org.springframework.web.bind.annotation.RequestParam;

public record SyncJobQueryRequest(
        @RequestParam(required = false) String jobType,
        @RequestParam(required = false) Long indexInfoId,
        @RequestParam(required = false) String baseDateFrom,
        @RequestParam(required = false) String baseDateTo,
        @RequestParam(required = false) String worker,
        @RequestParam(required = false) String jobTimeFrom,
        @RequestParam(required = false) String jobTimeTo,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) Long idAfter,
        @RequestParam(required = false) String cursor,
        @RequestParam(required = false, defaultValue = DEFAULT_SORT_FIELD) String sortField,
        @RequestParam(required = false, defaultValue = DEFAULT_SORT_DIRECTION) String sortDirection,
        @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) int size
) {
    private static final String DEFAULT_SORT_FIELD = "jobTime";
    private static final String DEFAULT_SORT_DIRECTION = "desc";
    private static final String DEFAULT_PAGE_SIZE = "10";
}
