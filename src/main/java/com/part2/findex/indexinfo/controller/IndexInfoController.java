package com.part2.findex.indexinfo.controller;

import com.part2.findex.indexinfo.dto.request.IndexSearchRequest;
import com.part2.findex.indexinfo.dto.response.IndexInfoDto;
import com.part2.findex.indexinfo.dto.response.PageResponse;
import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.indexinfo.service.IndexInfoService;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.Pageable;

@RestController
@RequestMapping("/api/index-infos")
@RequiredArgsConstructor
public class IndexInfoController {

    private final IndexInfoService indexInfoService;

    @GetMapping
    public ResponseEntity<PageResponse<IndexInfoDto>> findAllBySearchItem(
            IndexSearchRequest indexSearchRequest
    ) {
        System.out.println(indexSearchRequest.toString());

        PageResponse<IndexInfoDto> indexInfos = indexInfoService.findAllBySearchItem(indexSearchRequest);

        return ResponseEntity.status(HttpStatus.OK).body(indexInfos);
    }
}
