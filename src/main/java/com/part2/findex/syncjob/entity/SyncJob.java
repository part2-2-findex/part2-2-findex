package com.part2.findex.syncjob.entity;

import com.part2.findex.indexinfo.entity.IndexInfo;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;


@Entity
@Getter
@Table(name = "sync_jobs")
public class SyncJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_type", nullable = false)
    @Enumerated(EnumType.STRING)
    SyncJobType jobType;

    @Column(name = "target_date", nullable = false)
    LocalDate targetDate;

    @Column(name = "worker", nullable = false)
    String worker;

    @Column(name = "job_time", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    Instant jobTime;

    @Column(name = "result", nullable = false)
    @Enumerated(EnumType.STRING)
    SyncJobStatus result;

    @ManyToOne
    @JoinColumn(name = "index_info_id", nullable = false)
    IndexInfo indexInfo;

    protected SyncJob() {
    }

    public SyncJob(SyncJobType jobType,
                   LocalDate targetDate,
                   String worker,
                   Instant jobTime,
                   SyncJobStatus result,
                   IndexInfo indexInfo) {
        this.jobType = jobType;
        this.targetDate = targetDate;
        this.worker = worker;
        this.jobTime = jobTime;
        this.result = result;
        this.indexInfo = indexInfo;
    }
}
