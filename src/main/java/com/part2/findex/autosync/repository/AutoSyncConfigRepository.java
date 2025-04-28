package com.part2.findex.autosync.repository;

import com.part2.findex.autosync.entity.AutoSyncConfig;
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
                       (i.indexInfoBusinessKey.indexName > (SELECT ii.indexInfoBusinessKey.indexName FROM IndexInfo ii WHERE ii.id = 
                           (SELECT ai.indexInfo.id FROM AutoSyncConfig ai WHERE ai.id = :idAfter))
                       OR (i.indexInfoBusinessKey.indexName = (SELECT ii.indexInfoBusinessKey.indexName FROM IndexInfo ii WHERE ii.id = 
                           (SELECT ai.indexInfo.id FROM AutoSyncConfig ai WHERE ai.id = :idAfter))
                       AND a.id > :idAfter)))
                ORDER BY i.indexInfoBusinessKey.indexName ASC, a.id ASC
                LIMIT :size
            """)
    List<AutoSyncConfig> findAllByIndexNameAsc(
            @Param("indexInfoId") Long indexInfoId,
            @Param("enabled") Boolean enabled,
            @Param("idAfter") Long idAfter,
            @Param("size") int size
    );

    @Query("""
                SELECT a FROM AutoSyncConfig a
                LEFT JOIN FETCH a.indexInfo i
                WHERE (:indexInfoId IS NULL OR i.id = :indexInfoId)
                  AND (:enabled IS NULL OR a.enabled = :enabled)
                  AND (:idAfter IS NULL OR 
                       (i.indexInfoBusinessKey.indexName < (SELECT ii.indexInfoBusinessKey.indexName FROM IndexInfo ii WHERE ii.id = 
                           (SELECT ai.indexInfo.id FROM AutoSyncConfig ai WHERE ai.id = :idAfter))
                       OR (i.indexInfoBusinessKey.indexName = (SELECT ii.indexInfoBusinessKey.indexName FROM IndexInfo ii WHERE ii.id = 
                           (SELECT ai.indexInfo.id FROM AutoSyncConfig ai WHERE ai.id = :idAfter))
                       AND a.id < :idAfter)))
                ORDER BY i.indexInfoBusinessKey.indexName DESC, a.id DESC
                LIMIT :size
            """)
    List<AutoSyncConfig> findAllByIndexNameDesc(
            @Param("indexInfoId") Long indexInfoId,
            @Param("enabled") Boolean enabled,
            @Param("idAfter") Long idAfter,
            @Param("size") int size
    );

    @Query("""
                SELECT a FROM AutoSyncConfig a
                LEFT JOIN FETCH a.indexInfo i
                WHERE (:indexInfoId IS NULL OR i.id = :indexInfoId)
                  AND (:enabled IS NULL OR a.enabled = :enabled)
                  AND (:idAfter IS NULL OR 
                       (a.enabled > (SELECT ae.enabled FROM AutoSyncConfig ae WHERE ae.id = :idAfter)
                       OR (a.enabled = (SELECT ae.enabled FROM AutoSyncConfig ae WHERE ae.id = :idAfter)
                       AND a.id > :idAfter)))
                ORDER BY a.enabled ASC, a.id ASC
                LIMIT :size
            """)
    List<AutoSyncConfig> findAllByEnabledAsc(
            @Param("indexInfoId") Long indexInfoId,
            @Param("enabled") Boolean enabled,
            @Param("idAfter") Long idAfter,
            @Param("size") int size
    );

    @Query("""
                SELECT a FROM AutoSyncConfig a
                LEFT JOIN FETCH a.indexInfo i
                WHERE (:indexInfoId IS NULL OR i.id = :indexInfoId)
                  AND (:enabled IS NULL OR a.enabled = :enabled)
                  AND (:idAfter IS NULL OR 
                       (a.enabled < (SELECT ae.enabled FROM AutoSyncConfig ae WHERE ae.id = :idAfter)
                       OR (a.enabled = (SELECT ae.enabled FROM AutoSyncConfig ae WHERE ae.id = :idAfter)
                       AND a.id < :idAfter)))
                ORDER BY a.enabled DESC, a.id DESC
                LIMIT :size
            """)
    List<AutoSyncConfig> findAllByEnabledDesc(
            @Param("indexInfoId") Long indexInfoId,
            @Param("enabled") Boolean enabled,
            @Param("idAfter") Long idAfter,
            @Param("size") int size
    );

    List<AutoSyncConfig> findByEnabledTrue();
}
