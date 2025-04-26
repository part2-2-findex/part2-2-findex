package com.part2.findex.indexdata.repository.springjpa;

import com.part2.findex.indexdata.entity.IndexData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public interface SpringDataIndexDataRepository extends JpaRepository<IndexData, Long>, JpaSpecificationExecutor<IndexData> {

    @Query("SELECT i FROM IndexData i " +
            "JOIN i.indexInfo indexInfo " +
            "WHERE i.indexInfo.id = :indexInfoId " +
            "AND i.baseDate BETWEEN :startDate AND :endDate " +
            "AND (i.baseDate < :baseDateCursor OR (i.baseDate = :baseDateCursor AND i.id < :idCursor)) " +
            "ORDER BY i.baseDate DESC, i.closingPrice DESC, i.id DESC")
    List<IndexData> findAllByBaseDateDesc(
            @Param("indexInfoId") Long indexInfoId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("baseDateCursor") LocalDate baseDateCursor,
            @Param("idCursor") Long idCursor
    );

    @Query("SELECT i FROM IndexData i " +
            "JOIN i.indexInfo indexInfo " +
            "WHERE i.indexInfo.id = :indexInfoId " +
            "AND i.baseDate BETWEEN :startDate AND :endDate " +
            "AND (i.baseDate > :baseDateCursor OR (i.baseDate = :baseDateCursor AND i.id > :idCursor)) " +
            "ORDER BY i.baseDate ASC, i.closingPrice ASC, i.id ASC")
    List<IndexData> findAllByBaseDateAsc(
            @Param("indexInfoId") Long indexInfoId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("baseDateCursor") LocalDate baseDateCursor,
            @Param("idCursor") Long idCursor
    );

    @Query("SELECT i FROM IndexData i " +
            "JOIN i.indexInfo indexInfo " +
            "WHERE i.indexInfo.id = :indexInfoId " +
            "AND i.baseDate BETWEEN :startDate AND :endDate " +
            "AND (i.closingPrice > :closingPriceCursor OR (i.closingPrice = :closingPriceCursor AND i.id > :idCursor)) " +
            "ORDER BY i.closingPrice ASC, i.baseDate ASC, i.id ASC")
    List<IndexData> findAllByClosingPriceAsc(
            @Param("indexInfoId") Long indexInfoId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("closingPriceCursor") Double closingPriceCursor,
            @Param("idCursor") Long idCursor
    );


    @Query("SELECT i FROM IndexData i " +
            "JOIN i.indexInfo indexInfo " +
            "WHERE i.indexInfo.id = :indexInfoId " +
            "AND i.baseDate BETWEEN :startDate AND :endDate " +
            "AND (i.closingPrice < :closingPriceCursor OR (i.closingPrice = :closingPriceCursor AND i.id < :idCursor)) " +
            "ORDER BY i.closingPrice DESC, i.baseDate DESC, i.id DESC")
    List<IndexData> findAllByClosingPriceDesc(
            @Param("indexInfoId") Long indexInfoId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("closingPriceCursor") Double closingPriceCursor,
            @Param("idCursor") Long idCursor
    );


    @Query("SELECT COUNT(i) FROM IndexData i " +
            "JOIN i.indexInfo indexInfo " +
            "WHERE (:indexInfoId IS NULL OR i.indexInfo.id = :indexInfoId) " +
            "AND i.baseDate BETWEEN :startDate AND :endDate")
    Long countAllByFilters(
            @Param("indexInfoId") Long indexInfoId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

}
