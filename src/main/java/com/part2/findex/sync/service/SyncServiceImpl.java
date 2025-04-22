package com.part2.findex.sync.service;

import com.part2.findex.sync.dto.SyncJobResult;
import com.part2.findex.sync.repository.SyncJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SyncServiceImpl implements SyncService {
    private final SyncJobRepository syncJobRepository;

    @Override
    public List<SyncJobResult> synchronizeIndexInfo() {
        return null;
    }

    @Override
    public List<SyncJobResult> synchronizeIndexData() {
        return null;
    }

    @Override
    public List<SyncJobResult> getIndexSyncHistories() {
        return null;
    }
}
