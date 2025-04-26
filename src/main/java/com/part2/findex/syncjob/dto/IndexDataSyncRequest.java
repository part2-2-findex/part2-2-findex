package com.part2.findex.syncjob.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record IndexDataSyncRequest(
        List<Long> indexInfoIds,
        @NotNull LocalDate baseDateFrom,
        @NotNull LocalDate baseDateTo
) {
}