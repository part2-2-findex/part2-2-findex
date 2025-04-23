package com.part2.findex.autosyncconfig.repository;

import com.part2.findex.autosyncconfig.entity.AutoSyncConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutoSyncConfigRepository extends JpaRepository<AutoSyncConfig, Long> {
    @Query("""
        SELECT a FROM AutoSyncConfig a
        LEFT JOIN FETCH a.indexInfo i
        WHERE (:indexInfoId IS NULL OR i.id = :indexInfoId)
          AND (:enabled IS NULL OR a.enabled = :enabled)
          AND (:idAfter IS NULL OR 
               (CASE 
                    WHEN :sortField = 'indexInfo.indexName' AND :sortDirection = 'asc' 
                        THEN (i.indexName > (SELECT ii.indexName FROM IndexInfo ii WHERE ii.id = 
                            (SELECT ai.indexInfo.id FROM AutoSyncConfig ai WHERE ai.id = :idAfter)) 
                            OR (i.indexName = (SELECT ii.indexName FROM IndexInfo ii WHERE ii.id = 
                            (SELECT ai.indexInfo.id FROM AutoSyncConfig ai WHERE ai.id = :idAfter)) 
                            AND a.id > :idAfter))
                    WHEN :sortField = 'indexInfo.indexName' AND :sortDirection = 'desc' 
                        THEN (i.indexName < (SELECT ii.indexName FROM IndexInfo ii WHERE ii.id = 
                            (SELECT ai.indexInfo.id FROM AutoSyncConfig ai WHERE ai.id = :idAfter)) 
                            OR (i.indexName = (SELECT ii.indexName FROM IndexInfo ii WHERE ii.id = 
                            (SELECT ai.indexInfo.id FROM AutoSyncConfig ai WHERE ai.id = :idAfter)) 
                            AND a.id < :idAfter))
                    WHEN :sortField = 'enabled' AND :sortDirection = 'asc' 
                        THEN (a.enabled > (SELECT ae.enabled FROM AutoSyncConfig ae WHERE ae.id = :idAfter) 
                            OR (a.enabled = (SELECT ae.enabled FROM AutoSyncConfig ae WHERE ae.id = :idAfter) 
                            AND a.id > :idAfter))
                    WHEN :sortField = 'enabled' AND :sortDirection = 'desc' 
                        THEN (a.enabled < (SELECT ae.enabled FROM AutoSyncConfig ae WHERE ae.id = :idAfter) 
                            OR (a.enabled = (SELECT ae.enabled FROM AutoSyncConfig ae WHERE ae.id = :idAfter) 
                            AND a.id < :idAfter))
                    ELSE a.id > :idAfter
                END))
        ORDER BY 
            CASE 
                WHEN :sortField = 'indexInfo.indexName' AND :sortDirection = 'asc' THEN i.indexName
            END ASC,
            CASE 
                WHEN :sortField = 'indexInfo.indexName' AND :sortDirection = 'desc' THEN i.indexName
            END DESC,
            CASE 
                WHEN :sortField = 'enabled' AND :sortDirection = 'asc' THEN a.enabled
            END ASC,
            CASE 
                WHEN :sortField = 'enabled' AND :sortDirection = 'desc' THEN a.enabled
            END DESC,
            CASE 
                WHEN :sortDirection = 'asc' THEN a.id
            END ASC,
            CASE 
                WHEN :sortDirection = 'desc' THEN a.id
            END DESC
        LIMIT :size
        """
    )
    List<AutoSyncConfig> findByConditions(
            @Param("indexInfoId") Long indexInfoId,
            @Param("enabled") Boolean enabled,
            @Param("idAfter") Long idAfter,
            @Param("sortField") String sortField,
            @Param("sortDirection") String sortDirection,
            @Param("size") int size);

    List<AutoSyncConfig> findByEnabledTrue();
}
