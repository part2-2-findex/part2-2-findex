package com.part2.findex.indexinfo.service.strategy;

import com.part2.findex.indexinfo.entity.IndexInfo;

import java.util.List;

public interface SortedCallback {
    List<IndexInfo> call(String indexClassification, String indexName, Boolean favorite, String classificationCursor, Long idCursor);
}
