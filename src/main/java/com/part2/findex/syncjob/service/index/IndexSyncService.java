package com.part2.findex.syncjob.service.index;

import com.part2.findex.syncjob.dto.CursorPageResponseSyncJob;
import com.part2.findex.syncjob.dto.IndexDataSyncRequest;
import com.part2.findex.syncjob.dto.SyncJobQueryRequest;
import com.part2.findex.syncjob.dto.SyncJobResult;

import java.util.List;

public interface IndexSyncService {

    /**
     * 외부 OpenAPI에서 최신 지수 정보를 조회하고, DB와 동기화합니다.
     */
    List<SyncJobResult> synchronizeIndexInfo();

    /**
     * 지정한 기간의 지수 데이터를 외부 OpenAPI에서 조회하여 DB와 동기화합니다.
     */
    List<SyncJobResult> synchronizeIndexData(IndexDataSyncRequest indexDataSyncRequest);

    CursorPageResponseSyncJob getSyncJobs(SyncJobQueryRequest request);
}
