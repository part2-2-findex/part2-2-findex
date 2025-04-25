package com.part2.findex.indexdata.service;

import com.part2.findex.indexdata.dto.IndexDataUpdateRequest;
import com.part2.findex.indexdata.entity.IndexData;
import com.part2.findex.indexdata.repository.IndexDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class IndexDataServiceImpl implements IndexDataService {

    private final IndexDataRepository indexDataRepository;

    @Transactional
    @Override
    public void updateIndexData(Long indexDataId, IndexDataUpdateRequest indexDataUpdateRequest) {
        IndexData indexData = indexDataRepository.findById(indexDataId)
                .orElseThrow(() -> new NoSuchElementException("Index data not found"));

        indexData.update(indexDataUpdateRequest);

    }

    @Transactional
    @Override
    public void deleteIndexData(Long indexDataId) {
        indexDataRepository.deleteById(indexDataId);
    }
}
