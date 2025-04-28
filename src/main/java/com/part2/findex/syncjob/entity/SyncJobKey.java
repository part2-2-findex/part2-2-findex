package com.part2.findex.syncjob.entity;

import com.part2.findex.indexinfo.entity.IndexInfo;

import java.time.LocalDate;

public record SyncJobKey(SyncJobType jobType,
                         LocalDate targetDate,
                         IndexInfo indexInfo) {
}