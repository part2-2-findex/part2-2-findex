package com.part2.findex.syncjob.repository;

import com.part2.findex.syncjob.constant.SyncJobStatus;
import com.part2.findex.syncjob.constant.SyncJobType;
import com.part2.findex.syncjob.entity.SyncJob;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SyncJobSpecification {

    public static Specification<SyncJob> filter(
            SyncJobType jobType,
            Long indexInfoId,
            LocalDate baseDateFrom,
            LocalDate baseDateTo,
            String worker,
            LocalDateTime jobTimeFrom,
            LocalDateTime jobTimeTo,
            SyncJobStatus status
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (jobType != null) {
                predicates.add(cb.equal(root.get("jobType"), jobType));
            }

            if (indexInfoId != null) {
                predicates.add(cb.equal(root.get("indexInfo").get("id"), indexInfoId));
            }

            if (baseDateFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("targetDate"), baseDateFrom));
            }

            if (baseDateTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("targetDate"), baseDateTo));
            }

            if (worker != null) {
                predicates.add(cb.equal(root.get("worker"), worker));
            }

            if (jobTimeFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("jobTime"), jobTimeFrom));
            }

            if (jobTimeTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("jobTime"), jobTimeTo));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("result"), status));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}