package com.part2.findex.sync.service;

import com.part2.findex.sync.dto.SyncJobResult;

import java.util.List;

public interface SyncService {
    List<SyncJobResult> synchronizeIndexInfo();

    List<SyncJobResult> synchronizeIndexData();

    List<SyncJobResult> getIndexSyncHistories();
}
