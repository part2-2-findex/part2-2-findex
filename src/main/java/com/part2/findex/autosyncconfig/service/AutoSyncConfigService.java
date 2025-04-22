package com.part2.findex.autosyncconfig.service;
import com.part2.findex.autosyncconfig.dto.AutoSyncConfigDto;
import com.part2.findex.autosyncconfig.dto.response.CursorPageResponse;
import com.part2.findex.autosyncconfig.entity.AutoSyncConfig;
import com.part2.findex.autosyncconfig.repository.AutoSyncConfigRepository;
import com.part2.findex.indexinfo.entity.IndexInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
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
                .orElseThrow(() -> new NoSuchElementException("Auto sync config not found"));

        config.changeEnabled(enabled);

        return AutoSyncConfigDto.fromEntity(config);
    }

    @Transactional(readOnly = true)
    public CursorPageResponse<AutoSyncConfigDto> findAll(Long indexInfoId, Boolean enabled, Long idAfter, Long cursor, Pageable pageable) {
        Slice<AutoSyncConfigDto> slice = autoSyncConfigRepository.findByConditionsWithCursor(indexInfoId, enabled, cursor, pageable)
                .map(AutoSyncConfigDto::fromEntity);

        Long nextCursor = null;

        if (!slice.getContent().isEmpty()) {
            nextCursor = slice.getContent().get(slice.getContent().size() - 1)
                    .id();
        }

        long totalElements = autoSyncConfigRepository.count();

        return CursorPageResponse.<AutoSyncConfigDto>builder()
                .content(slice.getContent())
                .nextCursor(String.valueOf(nextCursor))
                .nextIdAfter(String.valueOf(nextCursor))
                .size(pageable.getPageSize())
                .totalElements(totalElements)
                .hasNext(slice.hasNext())
                .build();
    }
}