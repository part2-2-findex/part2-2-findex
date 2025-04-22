package com.part2.findex.autosyncconfig.controller;

import com.part2.findex.autosyncconfig.dto.AutoSyncConfigDto;
import com.part2.findex.autosyncconfig.dto.AutoSyncConfigUpdateRequest;
import com.part2.findex.autosyncconfig.dto.response.CursorPageResponse;
import com.part2.findex.autosyncconfig.service.AutoSyncConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping
    public ResponseEntity<CursorPageResponse<AutoSyncConfigDto>> findAll(
            @RequestParam(required = false) Long indexInfoId,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) Long idAfter,
            @RequestParam(required = false) Long cursor,
            @PageableDefault(size = 10, sort = "indexInfo.indexName", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return new ResponseEntity<>(autoSyncConfigService.findAll(indexInfoId, enabled, idAfter, cursor, pageable), HttpStatus.OK);
    }
}
