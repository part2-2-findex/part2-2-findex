package com.part2.findex.syncjob.controller;

import com.part2.findex.syncjob.dto.SyncJobResult;
import com.part2.findex.syncjob.service.IndexSyncOrchestratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/sync-jobs")
public class SyncController {
    private final IndexSyncOrchestratorService indexSyncOrchestratorService;

    @PostMapping("index-infos")
    public ResponseEntity<List<SyncJobResult>> synchronizeIndexInfos() {
        return ResponseEntity.ok(indexSyncOrchestratorService.syncIndexInfoWithOpenAPI());
    }

    @PostMapping("index-data")
    public ResponseEntity<List<SyncJobResult>> synchronizeIndexData() {
        return ResponseEntity.ok(indexSyncOrchestratorService.synchronizeIndexData());
    }
}
