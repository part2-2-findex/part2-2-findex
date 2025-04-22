package com.part2.findex.autosyncconfig.repository;

import com.part2.findex.autosyncconfig.entity.AutoSyncConfig;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoSyncConfigRepository extends JpaRepository<AutoSyncConfig, Long> {
    @Query("""
        SELECT a FROM AutoSyncConfig a
        LEFT JOIN FETCH a.indexInfo i
        WHERE (:indexInfoId IS NULL OR i.id = :indexInfoId)
          AND (:enabled IS NULL OR a.enabled = :enabled)
          AND (:cursorId IS NULL OR a.id > :cursorId)
    """)
    Slice<AutoSyncConfig> findByConditionsWithCursor(
            @Param("indexInfoId") Long indexInfoId,
            @Param("enabled") Boolean enabled,
            @Param("cursorId") Long cursorId,
            Pageable pageable);
}
