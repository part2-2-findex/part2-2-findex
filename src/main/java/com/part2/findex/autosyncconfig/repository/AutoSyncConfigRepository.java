package com.part2.findex.autosyncconfig.repository;

import com.part2.findex.autosyncconfig.entity.AutoSyncConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoSyncConfigRepository extends JpaRepository<AutoSyncConfig, Long> {
}
