package com.part2.findex.syncjob.repository;

import com.part2.findex.syncjob.entity.SyncJob;
import com.part2.findex.syncjob.entity.SyncJobType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SyncJobRepository extends JpaRepository<SyncJob, Long> {

    List<SyncJob> findByTargetDateBetweenAndIndexInfoIdInAndJobType(
            LocalDate startDate,
            LocalDate endDate,
            List<Long> indexInfoIds,
            SyncJobType type
    );
}
