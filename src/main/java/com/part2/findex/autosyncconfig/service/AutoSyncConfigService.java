package com.part2.findex.autosyncconfig.service;

import com.part2.findex.autosyncconfig.dto.AutoSyncConfigDto;
import com.part2.findex.autosyncconfig.dto.response.CursorPageResponse;
import com.part2.findex.autosyncconfig.entity.AutoSyncConfig;
import com.part2.findex.autosyncconfig.exception.AutoSyncConfigErrorCode;
import com.part2.findex.autosyncconfig.exception.AutoSyncConfigException;
import com.part2.findex.autosyncconfig.repository.AutoSyncConfigRepository;
import com.part2.findex.indexinfo.entity.IndexInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
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

    @Transactional
    public AutoSyncConfigDto updateEnabled(Long configId, boolean enabled) {
        AutoSyncConfig config = autoSyncConfigRepository.findById(configId)
                .orElseThrow(() -> new AutoSyncConfigException(AutoSyncConfigErrorCode.CONFIG_NOT_FOUND));

        config.changeEnabled(enabled);

        return AutoSyncConfigDto.fromEntity(config);
    }

    @Transactional(readOnly = true)
    public CursorPageResponse<AutoSyncConfigDto> findAll(Long indexInfoId, Boolean enabled, Long idAfter, Long cursor, int size, String sortField, String sortDirection) {
        List<AutoSyncConfig> autoSyncConfigs = List.of();

        if ("indexInfo.indexName".equals(sortField)) { // 확인필요
            if ("asc".equalsIgnoreCase(sortDirection)) {
                autoSyncConfigs = autoSyncConfigRepository.findAllByIndexNameAsc(indexInfoId, enabled, idAfter, size + 1);
            } else {
                autoSyncConfigs = autoSyncConfigRepository.findAllByIndexNameDesc(indexInfoId, enabled, idAfter, size + 1);
            }
        } else if ("enabled".equals(sortField)) {
            if ("asc".equalsIgnoreCase(sortDirection)) {
                autoSyncConfigs = autoSyncConfigRepository.findAllByEnabledAsc(indexInfoId, enabled, idAfter, size + 1);
            } else {
                autoSyncConfigs = autoSyncConfigRepository.findAllByEnabledDesc(indexInfoId, enabled, idAfter, size + 1);
            }
        }

        List<AutoSyncConfigDto> results = autoSyncConfigs.stream()
                .map(AutoSyncConfigDto::fromEntity)
                .toList();

        boolean hasNext = results.size() > size;
        List<AutoSyncConfigDto> content = hasNext ? results.subList(0, size) : results;
        Long nextCursor = null;
        if (!content.isEmpty()) {
            nextCursor = content.get(content.size() - 1).id();
        }

        long totalElements = autoSyncConfigRepository.count();

        return CursorPageResponse.<AutoSyncConfigDto>builder()
                .content(content)
                .nextCursor(String.valueOf(nextCursor))
                .nextIdAfter(String.valueOf(nextCursor))
                .size(size)
                .totalElements(totalElements)
                .hasNext(hasNext)
                .build();
    }
}