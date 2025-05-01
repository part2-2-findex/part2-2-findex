package com.part2.findex.syncjob.repository;

import com.part2.findex.syncjob.entity.SyncJob;
import com.part2.findex.syncjob.entity.SyncJobBusinessKey;

import java.util.List;

public interface SyncJobRepositoryCustom {
    List<SyncJob> findByKeys(List<SyncJobBusinessKey> keys);
}