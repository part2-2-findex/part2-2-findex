package com.part2.findex.syncjob.service.index.info;

import com.part2.findex.autosync.entity.AutoSyncConfig;
import com.part2.findex.autosync.repository.AutoSyncConfigRepository;
import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.indexinfo.entity.IndexInfoBusinessKey;
import com.part2.findex.syncjob.dto.StockIndexInfoResult;
import com.part2.findex.syncjob.entity.SyncJob;
import com.part2.findex.syncjob.entity.SyncJobStatus;
import com.part2.findex.syncjob.entity.SyncJobType;
import com.part2.findex.syncjob.mapper.IndexInfoMapper;
import com.part2.findex.syncjob.repository.SyncJobRepository;
import com.part2.findex.syncjob.service.common.ClientIpResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static com.part2.findex.syncjob.service.common.LocalDateParser.parseLocalDate;

@Service
@RequiredArgsConstructor
public class IndexInfoSyncJobService {

    private static final double BASE_INDEX_TOLERANCE = 0.000001;
    private final ClientIpResolver clientIpResolver;
    private final AutoSyncConfigRepository autoSyncConfigRepository;
    private final SyncJobRepository syncJobRepository;

    public SyncJob saveIndexInfoSyncJobAndAutoSync(SyncJobType jobType, IndexInfo savedIndexInfo, SyncJobStatus status, String baseTime) {
        saveAutoSync(savedIndexInfo);
        return saveIndexInfoSyncJob(jobType, status, savedIndexInfo, baseTime);
    }

    public SyncJob saveIndexInfoSyncJob(SyncJobType jobType, SyncJobStatus status, IndexInfo savedIndexInfo, String baseTime) {
        return syncJobRepository.save(createSyncJob(jobType, savedIndexInfo, status, baseTime));
    }

    public IndexInfo updateIndexInfo(Map<IndexInfoBusinessKey, IndexInfo> allIndexInfos, StockIndexInfoResult stockIndexInfoResultToUpdate) {
        IndexInfo IndexInfoToUpdate = allIndexInfos.get(IndexInfoMapper.toIndexInfoBusinessKey(stockIndexInfoResultToUpdate));
        IndexInfoToUpdate.update(stockIndexInfoResultToUpdate.employedItemsCount(), stockIndexInfoResultToUpdate.basePointInTime(), stockIndexInfoResultToUpdate.baseIndex(), null, BASE_INDEX_TOLERANCE);
        return IndexInfoToUpdate;
    }

    private void saveAutoSync(IndexInfo savedIndexInfo) {
        AutoSyncConfig config = AutoSyncConfig.builder()
                .indexInfo(savedIndexInfo)
                .enabled(false)
                .build();
        autoSyncConfigRepository.save(config);
    }

    private SyncJob createSyncJob(SyncJobType jobType, IndexInfo indexInfo, SyncJobStatus status, String stockIndexBaseDate) {
        String clientIp = clientIpResolver.getClientIp();
        LocalDate baseDate = parseLocalDate(stockIndexBaseDate);

        return new SyncJob(jobType, baseDate, clientIp, LocalDateTime.now(), status, indexInfo);
    }

}
