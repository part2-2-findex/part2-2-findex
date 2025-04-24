package com.part2.findex.indexinfo.repository;

import com.part2.findex.indexinfo.entity.IndexInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;


public interface IndexInfoRepository {
    Page<IndexInfo> findAllBySearchItem(String indexClassification, String indexName, Boolean favorite,Pageable pageable);

    IndexInfo save(IndexInfo indexInfo);

    Optional<IndexInfo> findById(Long id);

}
