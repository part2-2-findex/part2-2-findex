package com.part2.findex.indexinfo.controller;

import com.part2.findex.indexinfo.dto.request.IndexInfoCreateRequest;
import com.part2.findex.indexinfo.dto.request.IndexInfoUpdateRequest;
import com.part2.findex.indexinfo.dto.request.IndexSearchRequest;
import com.part2.findex.indexinfo.dto.response.IndexInfoDto;
import com.part2.findex.indexinfo.dto.response.PageResponse;
import com.part2.findex.indexinfo.service.IndexInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/index-infos")
@RequiredArgsConstructor
public class IndexInfoController {

    private final IndexInfoService indexInfoService;

    @GetMapping
    public ResponseEntity<PageResponse<IndexInfoDto>> findAllBySearchItem(
            @Validated @ModelAttribute IndexSearchRequest indexSearchRequest
    ) {
        PageResponse<IndexInfoDto> indexInfos = indexInfoService.findAllBySearchItem(indexSearchRequest);

        return ResponseEntity.status(HttpStatus.OK).body(indexInfos);
    }

    @PostMapping
    public ResponseEntity<IndexInfoDto> save(
            @Validated @RequestBody IndexInfoCreateRequest indexInfoCreateRequest
    ){

        IndexInfoDto indexinfo = indexInfoService.create(indexInfoCreateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(indexinfo);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<IndexInfoDto> update(
            @PathVariable("id") Long id,
            @Validated @RequestBody IndexInfoUpdateRequest indexInfoUpdateRequest
    ){
        IndexInfoDto indexinfo = indexInfoService.update(id, indexInfoUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(indexinfo);
    }
}
