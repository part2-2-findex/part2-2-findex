package com.part2.findex.syncjob.service.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateParser {
    public static LocalDate parseLocalDate(String basePointInTime) {
        LocalDate baseDate;
        if (basePointInTime.contains("-")) {
            baseDate = LocalDate.parse(basePointInTime);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            baseDate = LocalDate.parse(basePointInTime, formatter);
        }

        return baseDate;
    }
}
