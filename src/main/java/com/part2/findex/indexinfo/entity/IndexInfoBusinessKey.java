package com.part2.findex.indexinfo.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.Objects;

@Getter
@Embeddable
public class IndexInfoBusinessKey {
    private String indexClassification;
    private String indexName;

    protected IndexInfoBusinessKey() {
    }

    public IndexInfoBusinessKey(String classification, String indexName) {
        this.indexClassification = classification;
        this.indexName = indexName;
    }

    // 시간이 없어서 못 고치는 중 외부 Dto에 의존하지 않게 수정예
//    public static IndexInfoBusinessKey from(StockIndexInfoResult stockIndexInfoResult) {
//        return new IndexInfoBusinessKey(
//                stockIndexInfoResult.indexClassification(),
//                stockIndexInfoResult.indexName());
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IndexInfoBusinessKey)) return false;

        IndexInfoBusinessKey infoBusinessKey = (IndexInfoBusinessKey) o;
        boolean isSameName = Objects.equals(indexName, infoBusinessKey.indexName);
        boolean isSameIndexClassification = Objects.equals(indexClassification, infoBusinessKey.indexClassification);

        return isSameName && isSameIndexClassification;
    }

    @Override
    public int hashCode() {
        int result = indexName.hashCode();
        result = 31 * result + indexClassification.hashCode();

        return result;
    }

//    @Override
//    public String toString() {
//        return "IndexInfoBusinessKey{" +
//                "indexClassification='" + indexClassification + '\'' +
//                ", indexName='" + indexName + '\'' +
//                '}';
//    }
}
