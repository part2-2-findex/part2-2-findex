package com.part2.findex.sync.dto;

import java.time.Instant;
import java.time.LocalDate;

public record SyncJobResult(
        Long id,
        String jobType,
        Long indexInfoId,
        LocalDate targetDate,
        String worker,
        Instant jobTime,
        String result
) {
}
