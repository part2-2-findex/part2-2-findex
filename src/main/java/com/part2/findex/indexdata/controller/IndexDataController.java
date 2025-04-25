package com.part2.findex.indexdata.controller;

import com.part2.findex.indexdata.service.IndexDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/index-data")
@RequiredArgsConstructor
public class IndexDataController {

    private final IndexDataService indexDataService;
}
