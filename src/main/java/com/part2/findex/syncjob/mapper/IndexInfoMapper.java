package com.part2.findex.syncjob.mapper;

import com.part2.findex.indexinfo.entity.IndexInfoBusinessKey;
import com.part2.findex.openapi.dto.StockDataResult;
import com.part2.findex.syncjob.dto.StockIndexInfoResult;

public class IndexInfoMapper {
    public static IndexInfoBusinessKey toIndexInfoBusinessKey(StockDataResult source) {
        return new IndexInfoBusinessKey(source.indexClassification(), source.indexName());
    }

    public static IndexInfoBusinessKey toIndexInfoBusinessKey(StockIndexInfoResult source) {
        return new IndexInfoBusinessKey(source.indexClassification(), source.indexName());
    }
}
