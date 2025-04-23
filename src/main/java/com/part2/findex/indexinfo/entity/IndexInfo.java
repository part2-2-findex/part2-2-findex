package com.part2.findex.indexinfo.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.Objects;

@Entity
@Getter
@Table(name="index_info")
public class IndexInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "index_classification", nullable = false, length = 255)
    private String indexClassification;

    @Column(name = "index_name", nullable = false, length = 255)
    private String indexName;

    @Column(name = "employed_items_count", nullable = false)
    private int employedItemsCount;

    @Column(name = "base_point_in_time", nullable = false, length = 255)
    private String basePointInTime;

    @Column(name = "base_index", nullable = false)
    private double baseIndex;

    @Column(name = "favorite", nullable = false)
    private boolean favorite;

    @Column(name = "source_type", nullable = false, length = 50)
    private String sourceType;

    protected IndexInfo() {}

    public IndexInfo(String indexClassification, String indexName,
                     int employedItemsCount, String basePointInTime,
                     double baseIndex, boolean favorite) {

        this.indexClassification = indexClassification;
        this.indexName = indexName;
        this.employedItemsCount = employedItemsCount;
        this.basePointInTime = basePointInTime;
        this.baseIndex = baseIndex;
        this.favorite = favorite;
        this.sourceType = "사용자 등록";

    }

    public void update(int employedItemsCount, String basePointInTime, double baseIndex, boolean favorite){
        this.employedItemsCount = employedItemsCount;
        this.basePointInTime = basePointInTime;
        this.baseIndex = baseIndex;
        this.favorite = favorite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndexInfo indexInfo = (IndexInfo) o;
        return id != null && Objects.equals(id, indexInfo.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}