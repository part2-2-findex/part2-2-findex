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
}
