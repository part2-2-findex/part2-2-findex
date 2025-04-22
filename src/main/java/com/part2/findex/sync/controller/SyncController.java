package com.part2.findex.sync.controller;

import com.part2.findex.sync.dto.SyncJobResult;
import com.part2.findex.sync.service.SyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/sync-jobs")
public class SyncController {
    private final SyncService syncService;

    @PostMapping("index-infos")
    public ResponseEntity<List<SyncJobResult>> synchronizeIndexInfos() {
        return ResponseEntity.ok(syncService.synchronizeIndexInfo());
    }

    @PostMapping("index-data")
    public ResponseEntity<List<SyncJobResult>> synchronizeIndexData() {
        return ResponseEntity.ok(syncService.synchronizeIndexData());
    }

    @GetMapping
    public ResponseEntity<List<SyncJobResult>> getIndexSyncHistories() {
        return ResponseEntity.ok(syncService.getIndexSyncHistories());
    }
}
