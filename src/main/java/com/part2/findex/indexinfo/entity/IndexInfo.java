package com.part2.findex.indexinfo.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "index_info")
public class IndexInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "indexClassification", column = @Column(name = "index_classification", nullable = false, length = 255)),
            @AttributeOverride(name = "indexName", column = @Column(name = "index_name", nullable = false, length = 255))
    })
    private IndexInfoBusinessKey indexInfoBusinessKey;

    @Column(name = "employed_items_count", nullable = false)
    private double employedItemsCount;

    @Column(name = "base_point_in_time", nullable = false, length = 255)
    private String basePointInTime;

    @Column(name = "base_index", nullable = false)
    private double baseIndex;

    @Column(name = "favorite", nullable = false)
    private boolean favorite;

    @Column(name = "source_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private SourceType sourceType;

    protected IndexInfo() {
    }

    public IndexInfo(String indexClassification, String indexName,
                     double employedItemsCount, String basePointInTime,
                     double baseIndex, boolean favorite, SourceType sourceType) {
        this.indexInfoBusinessKey = new IndexInfoBusinessKey(indexClassification, indexName);
        this.employedItemsCount = employedItemsCount;
        this.basePointInTime = basePointInTime;
        this.baseIndex = baseIndex;
        this.favorite = favorite;
        this.sourceType = sourceType;
    }


    public void update(double employedItemsCount, String basePointInTime, double baseIndex, Boolean favorite,
                       double baseIndexTolerance) {
        if (Math.abs(this.employedItemsCount - employedItemsCount) > baseIndexTolerance) {
            this.employedItemsCount = employedItemsCount;
        }

        if (basePointInTime != null && !this.basePointInTime.equals(basePointInTime)) {
            this.basePointInTime = basePointInTime;
        }

        if (Math.abs(this.baseIndex - baseIndex) > baseIndexTolerance) {
            this.baseIndex = baseIndex;
        }

        if (favorite != null && this.favorite != favorite) {
            this.favorite = favorite;
        }
    }

    public String getIndexClassification() {
        return indexInfoBusinessKey.getIndexClassification();
    }

    public String getIndexName() {
        return indexInfoBusinessKey.getIndexName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IndexInfo)) return false;

        IndexInfo indexInfo = (IndexInfo) o;
        return indexInfoBusinessKey.equals(indexInfo.indexInfoBusinessKey);
    }

    @Override
    public int hashCode() {
        return indexInfoBusinessKey.hashCode();
    }
}