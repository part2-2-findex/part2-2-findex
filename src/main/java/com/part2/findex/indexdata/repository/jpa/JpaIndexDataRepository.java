package com.part2.findex.indexdata.repository.jpa;

import com.part2.findex.indexdata.entity.IndexData;
import com.part2.findex.indexdata.repository.IndexDataRepository;
import com.part2.findex.indexdata.repository.springjpa.SpringDataIndexDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class JpaIndexDataRepository implements IndexDataRepository {

    private final SpringDataIndexDataRepository indexDataRepository;

    public Optional<IndexData> findById(Long indexDataId) {
        return indexDataRepository.findById(indexDataId);
    }

    @Override
    public void deleteById(Long indexDataId) {
        indexDataRepository.deleteById(indexDataId);
    }
}
