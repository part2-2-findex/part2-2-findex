package com.part2.findex.autosync.scheduler;

import com.part2.findex.autosync.entity.AutoSyncConfig;
import com.part2.findex.autosync.repository.AutoSyncConfigRepository;
import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.syncjob.dto.IndexDataSyncRequest;
import com.part2.findex.syncjob.service.IndexSyncOrchestratorService;
import com.part2.findex.syncjob.service.impl.IndexDateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class IndexDataBatchScheduler {
    private final AutoSyncConfigRepository autoSyncConfigRepository;
    private final IndexSyncOrchestratorService indexSyncOrchestratorService;
    private final IndexDateService indexDateService;

    @Scheduled(cron = "0 0 14 * * *")
    public void syncIndexData() {
        List<Long> enabledIndexInfoIds = autoSyncConfigRepository.findByEnabledTrue()
                .stream()
                .map(AutoSyncConfig::getIndexInfo)
                .map(IndexInfo::getId)
                .toList();

        LocalDate targetDate = indexDateService.getLatestBusinessDay();
        IndexDataSyncRequest indexDataSyncRequest = new IndexDataSyncRequest(enabledIndexInfoIds, targetDate, targetDate);
        indexSyncOrchestratorService.synchronizeIndexData(indexDataSyncRequest);
    }
}
