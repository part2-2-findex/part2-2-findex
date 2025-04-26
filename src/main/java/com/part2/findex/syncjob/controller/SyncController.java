package com.part2.findex.syncjob.controller;

import com.part2.findex.syncjob.dto.CursorPageResponseSyncJob;
import com.part2.findex.syncjob.dto.IndexDataSyncRequest;
import com.part2.findex.syncjob.dto.SyncJobQueryRequest;
import com.part2.findex.syncjob.dto.SyncJobResult;
import com.part2.findex.syncjob.service.IndexSyncOrchestratorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/sync-jobs")
public class SyncController {
    private final IndexSyncOrchestratorService indexSyncOrchestratorService;

    @PostMapping("index-infos")
    public ResponseEntity<List<SyncJobResult>> synchronizeIndexInfos() {
        return ResponseEntity.ok(indexSyncOrchestratorService.syncIndexInfoWithOpenAPI());
    }

    @PostMapping("index-data")
    public ResponseEntity<List<SyncJobResult>> synchronizeIndexData(@Valid @RequestBody IndexDataSyncRequest indexDataSyncRequest) {
        return ResponseEntity.ok(indexSyncOrchestratorService.synchronizeIndexData(indexDataSyncRequest));
    }

    @GetMapping
    public ResponseEntity<CursorPageResponseSyncJob> getSyncJobs(
            SyncJobQueryRequest request
    ) {
        return ResponseEntity.ok(indexSyncOrchestratorService.getSyncJobs(request));
    }
}
