package com.part2.findex.indexdata.repository.springjpa;

import com.part2.findex.indexdata.entity.IndexData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Component;

@Component
public interface SpringDataIndexDataRepository extends JpaRepository<IndexData, Long>, JpaSpecificationExecutor<IndexData> {
}
