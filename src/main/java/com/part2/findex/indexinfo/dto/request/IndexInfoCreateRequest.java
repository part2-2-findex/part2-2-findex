package com.part2.findex.indexinfo.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class IndexInfoCreateRequest {
    @NotNull
    String indexClassification;

    @NotNull
    String indexName;

    @NotNull
    @Min(value = 1)
    int employedItemsCount;

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate basePointInTime;

    @Min(value = 1)
    int baseIndex;

    @NotNull
    Boolean favorite;
}
