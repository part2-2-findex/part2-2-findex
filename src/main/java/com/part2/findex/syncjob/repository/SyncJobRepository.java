package com.part2.findex.syncjob.repository;

import com.part2.findex.syncjob.entity.SyncJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SyncJobRepository extends JpaRepository<SyncJob, Long> {
}
