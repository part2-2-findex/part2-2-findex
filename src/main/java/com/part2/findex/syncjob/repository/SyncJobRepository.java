package com.part2.findex.syncjob.repository;

import com.part2.findex.syncjob.constant.SyncJobType;
import com.part2.findex.syncjob.entity.SyncJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;


public interface SyncJobRepository extends JpaRepository<SyncJob, Long>, JpaSpecificationExecutor<SyncJob>, SyncJobRepositoryCustom {

    List<SyncJob> findByTargetDateBetweenAndIndexInfoIdInAndJobType(
            LocalDate startDate,
            LocalDate endDate,
            List<Long> indexInfoIds,
            SyncJobType type
    );
}
