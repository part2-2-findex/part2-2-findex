package com.part2.findex.autosync.dto;

import com.part2.findex.autosync.entity.AutoSyncConfig;
import com.part2.findex.indexinfo.entity.IndexInfo;

public record AutoSyncConfigDto(
        Long id,
        Long indexInfoId,
        String indexClassification,
        String indexName,
        boolean enabled
) {
    public static AutoSyncConfigDto fromEntity(AutoSyncConfig config) {
        IndexInfo indexInfo = config.getIndexInfo();
        return new AutoSyncConfigDto(
                config.getId(),
                indexInfo.getId(),
                indexInfo.getIndexClassification(),
                indexInfo.getIndexName(),
                config.isEnabled()
        );
    }
}