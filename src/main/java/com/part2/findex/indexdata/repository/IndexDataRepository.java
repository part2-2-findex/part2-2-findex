package com.part2.findex.indexdata.repository;

import com.part2.findex.indexdata.entity.IndexData;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IndexDataRepository {

    List<IndexData> findAllByBaseDateAsc(Long indexInfoId, LocalDate startDate, LocalDate endDate, LocalDate classificationCursor, Long idCursor);

    List<IndexData> findAllByBaseDateDesc(Long indexInfoId, LocalDate startDate, LocalDate endDate, LocalDate classificationCursor, Long idCursor);

    List<IndexData> findAllByClosingPriceAsc(Long indexInfoId, LocalDate startDate, LocalDate endDate, Double classificationCursor, Long idCursor);

    List<IndexData> findAllByClosingPriceDesc(Long indexInfoId, LocalDate startDate, LocalDate endDate, Double classificationCursor, Long idCursor);

    Optional<IndexData> findById(Long indexDataId);

    void deleteById(Long indexDataId);

    void save(IndexData indexData);

    Long countAllByFilters(Long indexInfoId, LocalDate startDate, LocalDate endDate);
}
