package com.part2.findex.indexinfo.service.strategy;


import com.part2.findex.indexinfo.dto.CursorInfoDto;
import com.part2.findex.indexinfo.dto.request.IndexSearchRequest;
import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.indexinfo.entity.OrderType;
import com.part2.findex.indexinfo.repository.IndexInfoRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SortStrategyContext {

    private final IndexInfoRepository indexInfoRepository;
    private final SortedTemplate sortedTemplate;

    private final Map<String, SortedCallback> strategyMap = new HashMap<>();

    @PostConstruct
    public void initStrategyMap() {
        strategyMap.put(OrderType.INDEXCLASSIFICATION_ASC.name(), indexInfoRepository::findAllByClassificationAsc);
        strategyMap.put(OrderType.INDEXCLASSIFICATION_DESC.name(), indexInfoRepository::findAllByClassificationDesc);
        strategyMap.put(OrderType.INDEXNAME_ASC.name(), indexInfoRepository::findAllByNameAsc);
        strategyMap.put(OrderType.INDEXNAME_DESC.name(), indexInfoRepository::findAllByNameDesc);
    }

    public List<IndexInfo> findAllBySearch(IndexSearchRequest request, CursorInfoDto cursorInfoDto) {
        String key = request.getSortField().toUpperCase().concat("_").concat(request.getSortDirection().toUpperCase());

        SortedCallback callback = strategyMap.get(key);

        if (callback == null) {
            throw new IllegalArgumentException("Invalid sortField or sortDirection");
        }

        return sortedTemplate.execute(request, cursorInfoDto, callback);
    }
}
