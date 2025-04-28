package com.part2.findex.syncjob.service.impl;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class IndexDateService {

    public LocalDate getLatestBusinessDay() {
        LocalDate now = LocalDate.now();
        LocalTime nowTime = LocalTime.now();

        if (nowTime.isBefore(LocalTime.of(13, 59))) {
            now = now.minusDays(1);
        }

        while (isWeekend(now)) {
            now = now.minusDays(1);
        }

        return now;
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }
}
