package com.part2.findex.syncjob.dto;

import java.util.List;

public record CursorPageResponseSyncJob(
        List<SyncJobResult> syncJobs,
        String nextCursor,
        Long nextIdAfter,
        int size,
        long totalElements,
        boolean hasNext
) {
}