package com.part2.findex.syncjob.entity;

import org.springframework.data.domain.Sort;

import static com.part2.findex.syncjob.constant.SortDirectionConstant.*;

public enum SyncJobSortStrategy {

    JOB_TIME {
        @Override
        public Sort calculateSort(String direction) {
            if (direction.equalsIgnoreCase(ASCENDING_SORT_DIRECTION)) {
                return Sort.by(Sort.Order.asc(JOB_TIME_SORT_FIELD));
            }

            return Sort.by(Sort.Order.desc(JOB_TIME_SORT_FIELD));
        }
    },
    TARGET_DATE {
        @Override
        public Sort calculateSort(String direction) {
            if (direction.equalsIgnoreCase(ASCENDING_SORT_DIRECTION)) {
                return Sort.by(Sort.Order.asc(TARGET_DATE_SORT_FIELD), Sort.Order.asc(DEFAULT_SORT_FIELD));
            }

            return Sort.by(Sort.Order.desc(TARGET_DATE_SORT_FIELD), Sort.Order.desc(DEFAULT_SORT_FIELD));
        }
    },

    CREATED_AT {
        @Override
        Sort calculateSort(String direction) {
            if (direction.equalsIgnoreCase(ASCENDING_SORT_DIRECTION)) {
                return Sort.by(Sort.Order.asc(DEFAULT_SORT_FIELD));
            }

            return Sort.by(Sort.Order.desc(DEFAULT_SORT_FIELD));
        }
    };

    abstract Sort calculateSort(String direction);

    public static Sort fromField(String sortField) {
        return switch (sortField) {
            case JOB_TIME_SORT_FIELD -> JOB_TIME.calculateSort(sortField);
            case TARGET_DATE_SORT_FIELD -> TARGET_DATE.calculateSort(sortField);

            default -> CREATED_AT.calculateSort(sortField);
        };
    }

}
