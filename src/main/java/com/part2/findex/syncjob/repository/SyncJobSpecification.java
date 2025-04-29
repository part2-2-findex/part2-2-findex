package com.part2.findex.syncjob.repository;

import com.part2.findex.syncjob.constant.SortField;
import com.part2.findex.syncjob.dto.SyncJobQueryRequest;
import com.part2.findex.syncjob.entity.SyncJob;
import com.part2.findex.syncjob.entity.SyncJobStatus;
import com.part2.findex.syncjob.entity.SyncJobType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.part2.findex.syncjob.constant.SortField.jobTime;
import static com.part2.findex.syncjob.constant.SortField.targetDate;

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

    public static Specification<SyncJob> getSyncJobSpecification(SyncJobQueryRequest request, Specification<SyncJob> baseFilter, Sort sort) {
        Specification<SyncJob> finalFilter = Specification.where(baseFilter);

        if (request.cursor() != null && request.sortField().equals(jobTime.name())) {
            LocalDateTime cursor = LocalDateTime.parse(request.cursor());

            if (sort.getOrderFor(jobTime.name()).isAscending()) {
                finalFilter = finalFilter.and((root, query, cb) -> cb.greaterThan(root.get(request.sortField()), cursor));
            } else {
                finalFilter = finalFilter.and((root, query, cb) -> cb.lessThan(root.get(request.sortField()), cursor));
            }
        }
        if (request.cursor() != null && request.sortField().equals(SortField.targetDate.name())) {
            LocalDate cursor = LocalDate.parse(request.cursor());

            finalFilter = finalFilter.and((root, query, cb) -> {
                if (sort.getOrderFor(targetDate.name()).isAscending()) {
                    Predicate greaterTargetDate = cb.greaterThan(root.get("targetDate"), cursor);
                    Predicate equalTargetDateAndGreaterId = cb.and(
                            cb.equal(root.get("targetDate"), cursor),
                            cb.greaterThan(root.get("id"), request.idAfter())
                    );
                    return cb.or(greaterTargetDate, equalTargetDateAndGreaterId);
                } else {
                    Predicate lessTargetDate = cb.lessThan(root.get("targetDate"), cursor);
                    Predicate equalTargetDateAndLessId = cb.and(
                            cb.equal(root.get("targetDate"), cursor),
                            cb.lessThan(root.get("id"), request.idAfter())
                    );
                    return cb.or(lessTargetDate, equalTargetDateAndLessId);
                }
            });
        }

        return finalFilter;
    }
}