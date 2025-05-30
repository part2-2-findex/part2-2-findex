package com.part2.findex.syncjob.service.index.data;

import com.part2.findex.indexdata.entity.IndexData;
import com.part2.findex.syncjob.entity.SyncJob;
import com.part2.findex.syncjob.entity.SyncJobStatus;
import com.part2.findex.syncjob.entity.SyncJobType;
import com.part2.findex.syncjob.repository.SyncJobRepository;
import com.part2.findex.syncjob.service.common.ClientIpResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.part2.findex.syncjob.service.common.LocalDateParser.parseLocalDate;

@Service
@RequiredArgsConstructor
public class IndexDataSyncJobService {

    private final ClientIpResolver clientIpResolver;
    private final SyncJobRepository syncJobRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SyncJob saveIndexDataSyncJob(SyncJobType jobType, IndexData indexData, SyncJobStatus syncJobStatus) {
        return syncJobRepository.save(createIndexDataSyncJob(jobType, indexData, syncJobStatus));
    }

    private SyncJob createIndexDataSyncJob(SyncJobType jobType, IndexData indexData, SyncJobStatus status) {
        String clientIp = clientIpResolver.getClientIp();
        LocalDate baseDate = parseLocalDate(indexData.getBaseDate().toString());

        return new SyncJob(jobType, baseDate, clientIp, LocalDateTime.now(), status, indexData.getIndexInfo());
    }

}
