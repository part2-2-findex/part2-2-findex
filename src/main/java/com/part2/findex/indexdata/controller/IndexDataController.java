package com.part2.findex.indexdata.controller;

import com.part2.findex.indexdata.dto.IndexDataCreateRequest;
import com.part2.findex.indexdata.dto.IndexDataDto;
import com.part2.findex.indexdata.dto.IndexDataUpdateRequest;
import com.part2.findex.indexdata.dto.request.IndexDataRequest;
import com.part2.findex.indexdata.dto.response.IndexDataResponse;
import com.part2.findex.indexdata.service.IndexDataService;
import com.part2.findex.indexinfo.dto.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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

    @GetMapping
    public ResponseEntity<PageResponse<IndexDataResponse>> findAllBySearchItem(
            @Validated @ModelAttribute IndexDataRequest indexDataRequest
    ) {
        PageResponse<IndexDataResponse> indexInfos = indexDataService.findAllBySearchItem(indexDataRequest);
        return ResponseEntity.status(HttpStatus.OK).body(indexInfos);
    }


    @PatchMapping("/{indexDataId}")
    public ResponseEntity<?> updateIndexData(@PathVariable Long indexDataId, @RequestBody IndexDataUpdateRequest indexDataUpdateRequest) {
        IndexDataResponse indexDataDto =indexDataService.updateIndexData(indexDataId, indexDataUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(indexDataDto);
    }

    @DeleteMapping("/{indexDataId}")
    public ResponseEntity<IndexDataResponse> deleteIndexData(@PathVariable Long indexDataId) {
        indexDataService.deleteIndexData(indexDataId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/export/csv")
    public ResponseEntity<?> exportCsv(
        @RequestParam("indexInfoId") Long indexInfoId,
        @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @RequestParam("sortField") String sortField,
        @RequestParam("sortDirection") String sortDirection
    ) {
        byte[] csvBytes = indexDataService.getCsvData(indexInfoId, startDate, endDate, sortField, sortDirection);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename("index-data.csv").build());
        headers.setContentLength(csvBytes.length);
        headers.setContentType(MediaType.valueOf("text/csv"));

        return ResponseEntity
            .ok()
            .headers(headers)
            .body(csvBytes);
    }
}
