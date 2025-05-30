package com.part2.findex.indexinfo.repository;

import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.indexinfo.entity.IndexInfoBusinessKey;

import java.util.List;
import java.util.Optional;


public interface IndexInfoRepository {

    List<IndexInfo> findAll();

    List<IndexInfo> findAllByClassificationAsc(String indexClassification, String indexName, Boolean favorite, String classificationCursor, Long idCursor);

    List<IndexInfo> findAllByClassificationDesc(String indexClassification, String indexName, Boolean favorite, String classificationCursor, Long idCursor);

    List<IndexInfo> findAllByNameAsc(String indexClassification, String indexName, Boolean favorite, String classificationCursor, Long idCursor);

    List<IndexInfo> findAllByNameDesc(String indexClassification, String indexName, Boolean favorite, String classificationCursor, Long idCursor);

    IndexInfo save(IndexInfo indexInfo);

    Optional<IndexInfo> findById(Long id);

    void deleteById(Long id);

    Long countAllByFilters(String indexClassification, String indexName, Boolean favorite);

    List<IndexInfo> findAllById(List<Long> Ids);

    List<IndexInfo> findByIndexInfoBusinessKeys(List<IndexInfoBusinessKey> keys);
}
