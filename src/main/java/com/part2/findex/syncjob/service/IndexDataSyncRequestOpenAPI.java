package com.part2.findex.syncjob.service;

import java.time.LocalDate;

public record IndexDataSyncRequestOpenAPI(
        String name,
        LocalDate startDate,
        LocalDate untilDate
) {
}
