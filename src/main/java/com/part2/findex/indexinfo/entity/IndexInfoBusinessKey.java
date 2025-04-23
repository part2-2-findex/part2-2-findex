package com.part2.findex.indexinfo.entity;

import com.part2.findex.syncjob.dto.StockIndexInfoResult;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.Objects;

@Getter
@Embeddable
public class IndexInfoBusinessKey {

    @Column(name = "index_classification", nullable = false, length = 255)
    private String indexClassification;

    @Column(name = "index_name", nullable = false, length = 255)
    private String indexName;

    protected IndexInfoBusinessKey() {
    }

    public IndexInfoBusinessKey(String classification, String name) {
        this.indexClassification = classification;
        this.indexName = name;
    }

    public static IndexInfoBusinessKey from(StockIndexInfoResult stockIndexInfoResult) {
        return new IndexInfoBusinessKey(
                stockIndexInfoResult.indexClassification(),
                stockIndexInfoResult.indexName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IndexInfo)) return false;
        if (indexName == null || indexClassification == null) {
            return false;
        }
        IndexInfo indexInfo = (IndexInfo) o;
        boolean isSameName = Objects.equals(indexName, indexInfo.getIndexInfoBusinessKey().indexName);
        boolean isIndexClassification = Objects.equals(indexClassification, indexInfo.getIndexInfoBusinessKey().indexClassification);

        return isSameName && isIndexClassification;
    }

    @Override
    public int hashCode() {
        int result = indexName.hashCode();
        result = 31 * result + indexClassification.hashCode();

        return result;
    }

    @Override
    public String toString() {
        return "IndexInfoBusinessKey{" +
                "indexClassification='" + indexClassification + '\'' +
                ", indexName='" + indexName + '\'' +
                '}';
    }
}
