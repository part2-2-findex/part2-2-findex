package com.part2.findex.indexinfo.repository.jpa;

import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.indexinfo.repository.IndexInfoRepository;
import com.part2.findex.indexinfo.repository.springjpa.SpringDataIndexInfoRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class JpaIndexinfoRepository implements IndexInfoRepository {

    private final SpringDataIndexInfoRepository indexInfoRepository;

    @Override
    public Page<IndexInfo> findAllBySearchItem(String indexClassification, String indexName, Boolean favorite, Pageable pageable) {
        return indexInfoRepository.findAllBySearch(indexClassification, indexName, favorite, pageable);
    }

    @Override
    public IndexInfo save(IndexInfo indexInfo) {
        return indexInfoRepository.save(indexInfo);
    }
}
