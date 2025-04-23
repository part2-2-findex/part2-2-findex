package com.part2.findex.syncjob.controller;

import com.part2.findex.syncjob.dto.IndexDataSyncRequest;
import com.part2.findex.syncjob.dto.SyncJobResult;
import com.part2.findex.syncjob.service.IndexSyncOrchestratorService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
    private final IndexSyncOrchestratorService indexSyncOrchestratorService;

    @PostMapping("index-infos")
    public ResponseEntity<List<SyncJobResult>> synchronizeIndexInfos() {
        return ResponseEntity.ok(indexSyncOrchestratorService.syncIndexInfoWithOpenAPI());
    }

    @PostMapping("index-data")
    public ResponseEntity<List<SyncJobResult>> synchronizeIndexData(@RequestBody IndexDataSyncRequest indexDataSyncRequest) {
        return ResponseEntity.ok(indexSyncOrchestratorService.synchronizeIndexData(indexDataSyncRequest));
    }

    @GetMapping
    public ResponseEntity<List<SyncJobResult>> getIndexSyncHistories() {
        return ResponseEntity.ok(indexSyncOrchestratorService.getIndexSyncHistories());
    }
}
