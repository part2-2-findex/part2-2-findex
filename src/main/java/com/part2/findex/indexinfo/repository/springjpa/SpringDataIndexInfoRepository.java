package com.part2.findex.indexinfo.repository.springjpa;

import com.part2.findex.indexinfo.entity.IndexInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SpringDataIndexInfoRepository extends JpaRepository<IndexInfo, Long>, JpaSpecificationExecutor<IndexInfo> {

    @Query("SELECT i FROM IndexInfo i " +
            "WHERE (:indexClassification IS NULL OR i.indexClassification LIKE :indexClassification) " +
            "AND (:indexName IS NULL OR i.indexName LIKE :indexName) " +
            "AND (:favorite IS NULL OR i.favorite = :favorite) " +
            "AND (:nameCursor IS NULL OR (i.indexName > :nameCursor OR (i.indexName = :nameCursor AND i.id > :idCursor))) " +
            "ORDER BY i.indexName ASC, i.id ASC")
    List<IndexInfo> findAllByNameAsc(
            @Param("indexClassification") String indexClassification,
            @Param("indexName") String indexName,
            @Param("favorite") Boolean favorite,
            @Param("nameCursor") String nameCursor,
            @Param("idCursor") Long idCursor
    );


    @Query("SELECT i FROM IndexInfo i " +
            "WHERE (:indexClassification IS NULL OR i.indexClassification LIKE :indexClassification) " +
            "AND (:indexName IS NULL OR i.indexName LIKE :indexName) " +
            "AND (:favorite IS NULL OR i.favorite = :favorite) " +
            "AND (:nameCursor IS NULL OR (i.indexName < :nameCursor OR (i.indexName = :nameCursor AND i.id < :idCursor))) " +
            "ORDER BY i.indexName DESC, i.id DESC")
    List<IndexInfo> findAllByNameDesc(
            @Param("indexClassification") String indexClassification,
            @Param("indexName") String indexName,
            @Param("favorite") Boolean favorite,
            @Param("nameCursor") String nameCursor,
            @Param("idCursor") Long idCursor
    );


    // 분류명 기준 오름차순 커서 페이징
    @Query("SELECT i FROM IndexInfo i " +
            "WHERE (:indexClassification IS NULL OR i.indexClassification LIKE :indexClassification) " +
            "AND (:indexName IS NULL OR i.indexName LIKE :indexName) " +
            "AND (:favorite IS NULL OR i.favorite = :favorite) " +
            "AND (:classificationCursor IS NULL OR (i.indexClassification > :classificationCursor OR (i.indexClassification = :classificationCursor AND i.id > :idCursor))) " +
            "ORDER BY i.indexClassification ASC, i.id ASC")
    List<IndexInfo> findAllByClassificationAsc(
            @Param("indexClassification") String indexClassification,
            @Param("indexName") String indexName,
            @Param("favorite") Boolean favorite,
            @Param("classificationCursor") String classificationCursor,
            @Param("idCursor") Long idCursor
    );

    // 분류명 기준 내림차순 커서 페이징
    @Query("SELECT i FROM IndexInfo i " +
            "WHERE (:indexClassification IS NULL OR i.indexClassification LIKE :indexClassification) " +
            "AND (:indexName IS NULL OR i.indexName LIKE :indexName) " +
            "AND (:favorite IS NULL OR i.favorite = :favorite) " +
            "AND (:classificationCursor IS NULL OR (i.indexClassification < :classificationCursor OR (i.indexClassification = :classificationCursor AND i.id < :idCursor))) " +
            "ORDER BY i.indexClassification DESC, i.id DESC")
    List<IndexInfo> findAllByClassificationDesc(
            @Param("indexClassification") String indexClassification,
            @Param("indexName") String indexName,
            @Param("favorite") Boolean favorite,
            @Param("classificationCursor") String classificationCursor,
            @Param("idCursor") Long idCursor
    );


    @Query("SELECT COUNT(i) FROM IndexInfo i " +
            "WHERE (:indexClassification IS NULL OR i.indexClassification LIKE :indexClassification) " +
            "AND (:indexName IS NULL OR i.indexName LIKE :indexName) " +
            "AND (:favorite IS NULL OR i.favorite = :favorite)")
    Long countAllByFilters(
            @Param("indexClassification") String indexClassification,
            @Param("indexName") String indexName,
            @Param("favorite") Boolean favorite
    );

}
