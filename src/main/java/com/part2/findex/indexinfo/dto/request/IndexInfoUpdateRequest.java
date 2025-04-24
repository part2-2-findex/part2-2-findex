package com.part2.findex.indexinfo.dto.request;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class IndexInfoUpdateRequest {
    double employedItemsCount;

    LocalDate basePointInTime;

    double baseIndex;

    Boolean favorite;

    @Override
    public String toString() {
        return "IndexInfoUpdateRequest{" +
                "employedItemsCount=" + employedItemsCount +
                ", basePointInTime=" + basePointInTime +
                ", baseIndex=" + baseIndex +
                ", favorite=" + favorite +
                '}';
    }
}
