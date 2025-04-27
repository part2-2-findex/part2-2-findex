package com.part2.findex.autosync.scheduler;

import com.part2.findex.autosync.entity.AutoSyncConfig;
import com.part2.findex.autosync.repository.AutoSyncConfigRepository;
import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.syncjob.dto.IndexDataSyncRequest;
import com.part2.findex.syncjob.service.IndexSyncOrchestratorService;
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

    @Scheduled(cron = "0 * * * * *") // 1분마다 테스트 해주세요
    public void syncIndexData() {
        log.info("✅ 지수 데이터 자동 연동 배치 시작");
        List<Long> enabledIndexInfoIds = autoSyncConfigRepository.findByEnabledTrue()
                .stream()
                .map(AutoSyncConfig::getIndexInfo)
                .map(IndexInfo::getId)
                .toList();

        LocalDate today = LocalDate.now();
        IndexDataSyncRequest indexDataSyncRequest = new IndexDataSyncRequest(enabledIndexInfoIds, today, today);
        indexSyncOrchestratorService.synchronizeIndexData(indexDataSyncRequest);

        log.info("✅ 지수 데이터 자동 연동 배치 완료");
    }
}
