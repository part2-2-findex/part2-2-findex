package com.part2.findex.syncjob.dto;

import com.part2.findex.syncjob.constant.SortField;
import com.part2.findex.syncjob.entity.SyncJob;
import org.springframework.data.domain.Page;

import java.util.List;

public record CursorPageResponseSyncJob(
        List<SyncJobResult> content,
        String nextCursor,
        Long nextIdAfter,
        int size,
        long totalElements,
        boolean hasNext
) {
    public static CursorPageResponseSyncJob of(Page<SyncJob> syncJobPage, String sortField, long totalElements) {
        List<SyncJobResult> syncJobResults = syncJobPage.getContent()
                .stream()
                .map(SyncJobResult::from)
                .toList();
        String nextCursor = getNextCursor(syncJobPage, syncJobResults, sortField);
        Long lastSyncJobId = getLastSyncJobId(syncJobPage, syncJobResults);

        return new CursorPageResponseSyncJob(
                syncJobResults,
                nextCursor,
                lastSyncJobId,
                syncJobResults.size(),
                totalElements,
                syncJobPage.hasNext()
        );
    }

    private static Long getLastSyncJobId(Page<SyncJob> syncJobPage, List<SyncJobResult> syncJobResults) {
        Long lastSyncJobId = null;
        if (!syncJobResults.isEmpty() && syncJobPage.hasNext()) {
            lastSyncJobId = syncJobResults.get(syncJobResults.size() - 1).id();
        }
        return lastSyncJobId;
    }

    private static String getNextCursor(Page<SyncJob> syncJobPage, List<SyncJobResult> syncJobResults, String sortField) {
        String nextCursor = null;
        if (!syncJobResults.isEmpty() && sortField.equals(SortField.jobTime.name()) && syncJobPage.hasNext()) {
            nextCursor = syncJobResults.get(syncJobResults.size() - 1).jobTime().toString();
        }
        if (!syncJobResults.isEmpty() && sortField.equals(SortField.targetDate.name()) && syncJobPage.hasNext()) {
            nextCursor = syncJobResults.get(syncJobResults.size() - 1).targetDate().toString();
        }
        return nextCursor;
    }
}