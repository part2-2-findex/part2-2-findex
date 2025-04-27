package com.part2.findex.syncjob.mapper;

import com.part2.findex.indexinfo.entity.IndexInfoBusinessKey;
import com.part2.findex.openapi.dto.StockDataResult;

public class IndexInfoMapper {
    public static IndexInfoBusinessKey toIndexInfoBusinessKey(StockDataResult source) {
        return new IndexInfoBusinessKey(source.indexClassification(), source.indexName());
    }
}
