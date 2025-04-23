package com.part2.findex.syncjob.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class IndexSyncBatchService {
    private static final int BATCH_SIZE = 50;
    private final EntityManager entityManager;

    public <T, R> List<R> processBatch(List<T> items, Function<T, R> processor) {
        List<R> results = new ArrayList<>();
        int count = 0;

        for (T item : items) {
            R result = processor.apply(item);
            if (result != null) {
                results.add(result);
            }

            if (++count % BATCH_SIZE == 0) {
                flushAndClear();
            }
        }

        flushAndClear();
        return results;
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
