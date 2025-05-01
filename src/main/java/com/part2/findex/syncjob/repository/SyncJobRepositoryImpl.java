package com.part2.findex.syncjob.repository;

import com.part2.findex.syncjob.entity.QSyncJob;
import com.part2.findex.syncjob.entity.SyncJob;
import com.part2.findex.syncjob.entity.SyncJobBusinessKey;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class SyncJobRepositoryImpl implements SyncJobRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<SyncJob> findByKeys(List<SyncJobBusinessKey> keys) {
        QSyncJob syncJob = QSyncJob.syncJob;

        BooleanBuilder builder = new BooleanBuilder();
        for (SyncJobBusinessKey key : keys) {
            builder.or(
                    syncJob.jobType.eq(key.jobType())
                            .and(syncJob.targetDate.eq(key.targetDate()))
                            .and(syncJob.indexInfo.indexInfoBusinessKey.indexClassification.eq(key.indexInfo().getIndexClassification()))
                            .and(syncJob.indexInfo.indexInfoBusinessKey.indexName.eq(key.indexInfo().getIndexName()))
            );
        }

        return queryFactory
                .selectFrom(syncJob)
                .where(builder)
                .fetch();
    }
}
