package com.part2.findex.syncjob.service.impl;

import com.part2.findex.autosync.entity.AutoSyncConfig;
import com.part2.findex.autosync.repository.AutoSyncConfigRepository;
import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.indexinfo.entity.SourceType;
import com.part2.findex.indexinfo.repository.IndexInfoRepository;
import com.part2.findex.syncjob.dto.StockIndexInfoResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IndexInfoSyncService {
    private static final double BASE_INDEX_TOLERANCE = 0.000001;
    private final IndexInfoRepository indexInfoRepository;
    private final AutoSyncConfigRepository autoSyncConfigRepository;

    public IndexInfo updateIndexInfo(IndexInfo existingIndexInfo, StockIndexInfoResult updated) {
        existingIndexInfo.update(updated.employedItemsCount(), updated.basePointInTime(), updated.baseIndex(), null, BASE_INDEX_TOLERANCE);
        return existingIndexInfo;
    }

    public IndexInfo saveNewIndexInfo(StockIndexInfoResult newIndexInfo) {
        IndexInfo savedIndexInfo = indexInfoRepository.save(convertToIndexInfo(newIndexInfo));

        AutoSyncConfig config = AutoSyncConfig.builder()
                .indexInfo(savedIndexInfo)
                .enabled(false)
                .build();
        autoSyncConfigRepository.save(config);

        return savedIndexInfo;
    }

    public IndexInfo convertToIndexInfo(StockIndexInfoResult stockIndexInfo) {
        return new IndexInfo(
                stockIndexInfo.indexClassification(),
                stockIndexInfo.indexName(),
                stockIndexInfo.employedItemsCount(),
                stockIndexInfo.basePointInTime(),
                stockIndexInfo.baseIndex(),
                false,
                SourceType.OPEN_API);
    }
}