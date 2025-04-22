package com.part2.findex.indexinfo.repository.springjpa;

import com.part2.findex.indexinfo.entity.IndexInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.data.jpa.repository.JpaRepository;

@Component
public interface SpringDataIndexInfoRepository extends JpaRepository<IndexInfo, Long> {
    @Query("SELECT i FROM IndexInfo i WHERE i.indexClassification LIKE :indexClassification AND i.indexName LIKE :indexName AND (:favorite IS NULL OR i.favorite = :favorite)")
    Page<IndexInfo> findAllBySearch(@Param("indexClassification") String indexClassification,
                                    @Param("indexName") String indexName,
                                    @Param("favorite") Boolean favorite,
                                    Pageable pageable);
}
