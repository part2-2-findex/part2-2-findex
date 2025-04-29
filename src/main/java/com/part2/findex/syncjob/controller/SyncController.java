package com.part2.findex.syncjob.controller;

import com.part2.findex.syncjob.dto.CursorPageResponseSyncJob;
import com.part2.findex.syncjob.dto.IndexDataSyncRequest;
import com.part2.findex.syncjob.dto.SyncJobQueryRequest;
import com.part2.findex.syncjob.dto.SyncJobResult;
import com.part2.findex.syncjob.service.index.IndexSyncService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/sync-jobs")
public class SyncController {
    private final IndexSyncService indexSyncService;

    @PostMapping("index-infos")
    public ResponseEntity<List<SyncJobResult>> synchronizeIndexInfos() {
        return ResponseEntity.ok(indexSyncService.synchronizeIndexInfo());
    }

    @PostMapping("index-data")
    public ResponseEntity<List<SyncJobResult>> synchronizeIndexData(@Valid @RequestBody IndexDataSyncRequest indexDataSyncRequest) {
        return ResponseEntity.ok(indexSyncService.synchronizeIndexData(indexDataSyncRequest));
    }

    @GetMapping
    public ResponseEntity<CursorPageResponseSyncJob> getSyncJobs(SyncJobQueryRequest request) {
        return ResponseEntity.ok(indexSyncService.getSyncJobs(request));
    }
}
