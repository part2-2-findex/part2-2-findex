package com.part2.findex.syncjob.dto;

import com.part2.findex.indexinfo.entity.IndexInfo;

import java.time.LocalDate;

public record IndexDataOpenAPIRequest(
        String indexName,
        LocalDate startDate,
        LocalDate endDate
) {
    public static IndexDataOpenAPIRequest of(IndexInfo indexInfo, LocalDate startDate, LocalDate endDate) {
        return new IndexDataOpenAPIRequest(indexInfo.getIndexName(), startDate, endDate);
    }
}
