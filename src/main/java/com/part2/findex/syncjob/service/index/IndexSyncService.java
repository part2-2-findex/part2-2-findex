package com.part2.findex.syncjob.service.index;

import com.part2.findex.syncjob.dto.CursorPageResponseSyncJob;
import com.part2.findex.syncjob.dto.IndexDataSyncRequest;
import com.part2.findex.syncjob.dto.SyncJobQueryRequest;
import com.part2.findex.syncjob.dto.SyncJobResult;

import java.util.List;

public interface IndexSyncService {

    List<SyncJobResult> synchronizeIndexInfo();

    List<SyncJobResult> synchronizeIndexData(IndexDataSyncRequest indexDataSyncRequest);

    CursorPageResponseSyncJob getSyncJobs(SyncJobQueryRequest request);

}
