package com.part2.findex.indexdata.repository.jpa;

import com.part2.findex.indexdata.entity.IndexData;
import com.part2.findex.indexdata.repository.IndexDataRepository;
import com.part2.findex.indexdata.repository.springjpa.SpringDataIndexDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class JpaIndexDataRepository implements IndexDataRepository {

    private final SpringDataIndexDataRepository indexDataRepository;

    @Override
    public void save(IndexData indexData) {
        indexDataRepository.save(indexData);
    }

    @Override
    public List<IndexData> findAllByBaseDateAsc(Long indexInfoId, LocalDate startDate, LocalDate endDate, LocalDate baseDateCursor, Long idCursor) {
        System.out.println("findAllByBaseDateAsc");


        return indexDataRepository.findAllByBaseDateAsc(indexInfoId, startDate, endDate, baseDateCursor, idCursor);

    }

    @Override
    public List<IndexData> findAllByBaseDateDesc(Long indexInfoId, LocalDate startDate, LocalDate endDate, LocalDate baseDateCursor, Long idCursor) {
        System.out.println("findAllByBaseDateDesc");

        return indexDataRepository.findAllByBaseDateDesc(indexInfoId, startDate, endDate, baseDateCursor, idCursor);
    }

    @Override
    public List<IndexData> findAllByClosingPriceAsc(Long indexInfoId, LocalDate startDate, LocalDate endDate, Double closingPriceCursor, Long idCursor) {
        System.out.println("findAllByClosingPriceAsc");

        return indexDataRepository.findAllByClosingPriceAsc(indexInfoId, startDate, endDate, closingPriceCursor, idCursor);
    }

    @Override
    public List<IndexData> findAllByClosingPriceDesc(Long indexInfoId, LocalDate startDate, LocalDate endDate, Double closingPriceCursor, Long idCursor) {
        System.out.println("findAllByClosingPriceDesc");

        return indexDataRepository.findAllByClosingPriceDesc(indexInfoId, startDate, endDate, closingPriceCursor, idCursor);
    }

    public Optional<IndexData> findById(Long indexDataId) {
        return indexDataRepository.findById(indexDataId);
    }

    @Override
    public void deleteById(Long indexDataId) {
        indexDataRepository.deleteById(indexDataId);
    }

    @Override
    public Long countAllByFilters(Long indexInfoId, LocalDate startDate, LocalDate endDate) {
        return indexDataRepository.countAllByFilters(indexInfoId, startDate, endDate);
    }
}
