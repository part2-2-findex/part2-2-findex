package com.part2.findex.indexdata.service;

import com.part2.findex.indexdata.repository.IndexDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IndexDataServiceImpl implements IndexDataService {

    private final IndexDataRepository indexDataRepository;

}
