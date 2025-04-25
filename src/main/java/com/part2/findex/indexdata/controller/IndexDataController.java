package com.part2.findex.indexdata.controller;

import com.part2.findex.indexdata.dto.IndexDataCreateRequest;
import com.part2.findex.indexdata.dto.IndexDataDto;
import com.part2.findex.indexdata.dto.IndexDataUpdateRequest;
import com.part2.findex.indexdata.service.IndexDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/index-data")
@RequiredArgsConstructor
public class IndexDataController {

    private final IndexDataService indexDataService;

    @PostMapping
    public ResponseEntity<?> createIndexData(@RequestBody IndexDataCreateRequest indexDataCreateRequest) {
        IndexDataDto indexDataDto = indexDataService.createIndexData(indexDataCreateRequest);
        return ResponseEntity.status(201).body(indexDataDto);
    }

    @PatchMapping("/{indexDataId}")
    public ResponseEntity<?> updateIndexData(@PathVariable Long indexDataId, @RequestBody IndexDataUpdateRequest indexDataUpdateRequest) {
        indexDataService.updateIndexData(indexDataId, indexDataUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{indexDataId}")
    public ResponseEntity<?> deleteIndexData(@PathVariable Long indexDataId) {
        indexDataService.deleteIndexData(indexDataId);
        return ResponseEntity.noContent().build();
    }
}
