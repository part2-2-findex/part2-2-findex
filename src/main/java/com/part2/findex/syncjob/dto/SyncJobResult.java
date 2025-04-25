package com.part2.findex.syncjob.dto;

import com.part2.findex.syncjob.entity.SyncJob;

import java.time.Instant;
import java.time.LocalDate;

public record SyncJobResult(
        Long id,
        String jobType,
        Long indexInfoId,
        LocalDate targetDate,
        String worker,
        Instant jobTime,
        String result
) {

    public static SyncJobResult from(SyncJob syncJob) {

        return new SyncJobResult(syncJob.getId(),
                syncJob.getJobType().name(),
                syncJob.getIndexInfo().getId(),
                syncJob.getTargetDate(),
                syncJob.getWorker(),
                syncJob.getJobTime(),
                syncJob.getResult().name()
        );
    }
}
