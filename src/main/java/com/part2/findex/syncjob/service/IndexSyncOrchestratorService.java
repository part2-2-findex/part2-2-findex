package com.part2.findex.syncjob.service;

import com.part2.findex.syncjob.dto.SyncJobResult;

import java.util.List;

public interface IndexSyncOrchestratorService {
    List<SyncJobResult> syncIndexInfoWithOpenAPI();

    List<SyncJobResult> synchronizeIndexData();

    List<SyncJobResult> getIndexSyncHistories();
}
