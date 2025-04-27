package com.part2.findex.syncjob.repository;

import com.part2.findex.syncjob.entity.SyncJob;
import com.part2.findex.syncjob.entity.SyncJobKey;

import java.util.List;

public interface SyncJobRepositoryCustom {
    List<SyncJob> findByKeys(List<SyncJobKey> keys);
}