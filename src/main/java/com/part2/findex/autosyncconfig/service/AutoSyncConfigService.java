package com.part2.findex.autosyncconfig.service;
import com.part2.findex.autosyncconfig.entity.AutoSyncConfig;
import com.part2.findex.autosyncconfig.entity.IndexInfo;
import com.part2.findex.autosyncconfig.repository.AutoSyncConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AutoSyncConfigService {

    private final AutoSyncConfigRepository autoSyncConfigRepository;

    @Transactional
    public void createDefaultConfig(IndexInfo indexInfo) {
        AutoSyncConfig config = AutoSyncConfig.builder()
                .indexInfo(indexInfo)
                .enabled(false)
                .build();

        autoSyncConfigRepository.save(config);
    }
}