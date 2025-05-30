package com.part2.findex.indexinfo.repository.jpa;

import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.indexinfo.entity.IndexInfoBusinessKey;
import com.part2.findex.indexinfo.entity.QIndexInfo;
import com.part2.findex.indexinfo.repository.IndexInfoRepository;
import com.part2.findex.indexinfo.repository.springjpa.SpringDataIndexInfoRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaIndexInfoRepository implements IndexInfoRepository {

    private final SpringDataIndexInfoRepository indexInfoRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<IndexInfo> findAll() {
        return indexInfoRepository.findAll();
    }

    @Override
    public List<IndexInfo> findAllByClassificationAsc(String indexClassification, String indexName, Boolean favorite, String classificationCursor, Long idCursor
    ) {
        return indexInfoRepository.findAllByClassificationAsc(indexClassification, indexName, favorite, classificationCursor, idCursor);
    }

    @Override
    public List<IndexInfo> findAllByClassificationDesc(String indexClassification, String indexName, Boolean favorite, String classificationCursor, Long idCursor
    ) {
        return indexInfoRepository.findAllByClassificationDesc(indexClassification, indexName, favorite, classificationCursor, idCursor);
    }

    @Override
    public List<IndexInfo> findAllByNameAsc(String indexClassification, String indexName, Boolean favorite, String classificationCursor, Long idCursor
    ) {
        return indexInfoRepository.findAllByNameAsc(indexClassification, indexName, favorite, classificationCursor, idCursor);
    }

    @Override
    public List<IndexInfo> findAllByNameDesc(String indexClassification, String indexName, Boolean favorite, String classificationCursor, Long idCursor
    ) {
        return indexInfoRepository.findAllByNameDesc(indexClassification, indexName, favorite, classificationCursor, idCursor);
    }

    @Override
    public IndexInfo save(IndexInfo indexInfo) {
        return indexInfoRepository.save(indexInfo);
    }

    @Override
    public Optional<IndexInfo> findById(Long id) {
        return indexInfoRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        indexInfoRepository.deleteById(id);
    }

    @Override
    public Long countAllByFilters(String indexClassification, String indexName, Boolean favorite) {
        return indexInfoRepository.countAllByFilters(indexClassification, indexName, favorite);
    }

    @Override
    public List<IndexInfo> findAllById(List<Long> Ids) {
        return indexInfoRepository.findAllById(Ids);
    }

    @Override
    public List<IndexInfo> findByIndexInfoBusinessKeys(List<IndexInfoBusinessKey> keys) {
        QIndexInfo indexInfo = QIndexInfo.indexInfo;
        BooleanBuilder builder = new BooleanBuilder();
        for (IndexInfoBusinessKey key : keys) {
            builder.or(
                    indexInfo.indexInfoBusinessKey.indexClassification.eq(key.getIndexClassification())
                            .and(indexInfo.indexInfoBusinessKey.indexName.eq(key.getIndexName()))
            );
        }
        return queryFactory
                .selectFrom(indexInfo)
                .where(builder)
                .fetch();
    }
}
