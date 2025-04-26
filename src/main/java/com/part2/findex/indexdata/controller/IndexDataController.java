package com.part2.findex.indexdata.controller;

import com.part2.findex.indexdata.dto.IndexDataUpdateRequest;
import com.part2.findex.indexdata.dto.request.IndexDataRequest;
import com.part2.findex.indexdata.dto.response.IndexDataResponse;
import com.part2.findex.indexdata.service.IndexDataService;
import com.part2.findex.indexinfo.dto.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/index-data")
@RequiredArgsConstructor
public class IndexDataController {

    private final IndexDataService indexDataService;

    @GetMapping
    public ResponseEntity<PageResponse<IndexDataResponse>> findAllBySearchItem(
            @Validated @ModelAttribute IndexDataRequest indexDataRequest
    ) {
        PageResponse<IndexDataResponse> indexInfos = indexDataService.findAllBySearchItem(indexDataRequest);
        return ResponseEntity.status(HttpStatus.OK).body(indexInfos);
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
