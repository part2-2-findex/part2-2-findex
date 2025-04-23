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
import java.util.NoSuchElementException;

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
        List<AutoSyncConfigDto> results = autoSyncConfigRepository.findByConditions(indexInfoId, enabled, idAfter, sortField, sortDirection, size + 1)
                .stream()
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