package com.part2.findex.autosyncconfig.controller;

import com.part2.findex.autosyncconfig.dto.AutoSyncConfigDto;
import com.part2.findex.autosyncconfig.dto.AutoSyncConfigUpdateRequest;
import com.part2.findex.autosyncconfig.dto.response.CursorPageResponse;
import com.part2.findex.autosyncconfig.service.AutoSyncConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auto-sync-configs")
public class AutoSyncConfigController {
    private final AutoSyncConfigService autoSyncConfigService;

    @PatchMapping("/{id}")
    public ResponseEntity<AutoSyncConfigDto> updateEnabled(
            @PathVariable Long id,
            @RequestBody AutoSyncConfigUpdateRequest request
    ) {
        return new ResponseEntity<>(autoSyncConfigService.updateEnabled(id, request.enabled()), HttpStatus.OK);
    }
<<<<<<< HEAD
=======

    @GetMapping
    public ResponseEntity<CursorPageResponse<AutoSyncConfigDto>> findAll(
            @RequestParam(required = false) Long indexInfoId,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) Long idAfter,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "indexInfo.indexName") String sortField, // indexInfo.indexName 또는 enabled
            @RequestParam(defaultValue = "asc") String sortDirection // asc 또는 desc
    ) {
        return new ResponseEntity<>(autoSyncConfigService.findAll(indexInfoId, enabled, idAfter, cursor, size, sortField, sortDirection), HttpStatus.OK);
    }
>>>>>>> feature/54-예외-처리-공통화
}
