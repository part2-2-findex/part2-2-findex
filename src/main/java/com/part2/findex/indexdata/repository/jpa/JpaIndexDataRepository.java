package com.part2.findex.indexdata.repository.jpa;

import com.part2.findex.indexdata.entity.IndexData;
import com.part2.findex.indexdata.repository.IndexDataRepository;
import com.part2.findex.indexdata.repository.springjpa.SpringDataIndexDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class JpaIndexDataRepository implements IndexDataRepository {

    private final SpringDataIndexDataRepository indexDataRepository;
}
