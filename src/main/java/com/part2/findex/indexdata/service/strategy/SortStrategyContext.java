package com.part2.findex.indexdata.service.strategy;


import com.part2.findex.indexdata.dto.request.IndexDataRequest;
import com.part2.findex.indexdata.entity.IndexData;
import com.part2.findex.indexdata.entity.OrderType;
import com.part2.findex.indexdata.repository.IndexDataRepository;
import com.part2.findex.indexinfo.dto.CursorInfoDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("indexDataSortStrategyContext")
@RequiredArgsConstructor
public class SortStrategyContext {

    private final IndexDataRepository indexDataRepository;
    private final SortedTemplate sortedTemplate;

    private final Map<String, SortedCallback> strategyMap = new HashMap<>();

    @PostConstruct
    public void initStrategyMap() {
        strategyMap.put(OrderType.BASEDATE_ASC.name(), (SortedCallback<LocalDate>) indexDataRepository::findAllByBaseDateAsc);
        strategyMap.put(OrderType.BASEDATE_DESC.name(), (SortedCallback<LocalDate>) indexDataRepository::findAllByBaseDateDesc);
        strategyMap.put(OrderType.CLOSINGPRICE_ASC.name(), (SortedCallback<Double>) indexDataRepository::findAllByClosingPriceAsc);
        strategyMap.put(OrderType.CLOSINGPRICE_DESC.name(), (SortedCallback<Double>) indexDataRepository::findAllByClosingPriceDesc);
    }

    public List<IndexData> findAllBySearch(IndexDataRequest request, CursorInfoDto cursorInfoDto) {
        String key = request.getSortField().toUpperCase().concat("_").concat(request.getSortDirection().toUpperCase());

        SortedCallback callback = strategyMap.get(key);

        if (callback == null) {
            throw new IllegalArgumentException("Invalid sortField or sortDirection");
        }

        return sortedTemplate.execute(request, cursorInfoDto, callback);
    }
}
