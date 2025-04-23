package com.part2.findex.syncjob.dto;

import java.time.LocalDate;
import java.util.List;

public record IndexDataSyncRequest(
        List<Integer> indexInfoIds,
        LocalDate baseDateFrom,
        LocalDate baseDateTo
) {}