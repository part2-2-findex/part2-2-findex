package com.part2.findex.indexdata.repository;

import com.part2.findex.indexdata.entity.IndexData;

import java.util.Optional;

public interface IndexDataRepository {
    Optional<IndexData> findById(Long indexDataId);

    void deleteById(Long indexDataId);
}
