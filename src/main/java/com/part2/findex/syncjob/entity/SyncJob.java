package com.part2.findex.syncjob.entity;

import com.part2.findex.indexinfo.entity.IndexInfo;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "sync_job")
public class SyncJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SyncJobType jobType;

    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Column(name = "worker", nullable = false)
    private String worker;

    @Column(name = "job_time", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant jobTime;

    @Column(name = "result", nullable = false)
    @Enumerated(EnumType.STRING)
    private SyncJobStatus result;

    @ManyToOne
    @JoinColumn(name = "index_info_id", nullable = false)
    private IndexInfo indexInfo;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SyncJob)) return false;

        SyncJob syncJob = (SyncJob) o;

        if (jobType != syncJob.jobType) return false;
        if (!Objects.equals(targetDate, syncJob.targetDate)) return false;
        return Objects.equals(indexInfo, syncJob.indexInfo);
    }

    @Override
    public int hashCode() {
        int result = jobType != null ? jobType.hashCode() : 0;
        result = 31 * result + (targetDate != null ? targetDate.hashCode() : 0);
        result = 31 * result + (indexInfo != null ? indexInfo.hashCode() : 0);
        return result;
    }
}
