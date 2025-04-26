package com.part2.findex.syncjob.entity;

import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.syncjob.constant.SyncJobStatus;
import com.part2.findex.syncjob.constant.SyncJobType;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Getter
@Table(name = "sync_jobs")
@EntityListeners(AuditingEntityListener.class)
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
    LocalDateTime jobTime;

    @Column(name = "result", nullable = false)
    @Enumerated(EnumType.STRING)
    SyncJobStatus result;

    @ManyToOne
    @JoinColumn(name = "index_info_id", nullable = false)
    IndexInfo indexInfo;

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
                   LocalDateTime jobTime,
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
    public String toString() {
        return "SyncJob{" +
                "id=" + id +
                ", jobType=" + jobType +
                ", targetDate=" + targetDate +
                ", worker='" + worker + '\'' +
                ", jobTime=" + jobTime +
                ", result=" + result +
                ", indexInfo=" + indexInfo +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
