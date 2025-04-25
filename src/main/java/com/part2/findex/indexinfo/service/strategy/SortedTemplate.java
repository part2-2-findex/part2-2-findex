package com.part2.findex.indexinfo.service.strategy;

import com.part2.findex.indexinfo.dto.CursorInfoDto;
import com.part2.findex.indexinfo.dto.request.IndexSearchRequest;
import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.indexinfo.repository.springjpa.SpringDataIndexInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SortedTemplate {
    private final SpringDataIndexInfoRepository indexInfoRepository;

    public List<IndexInfo> execute(IndexSearchRequest request, CursorInfoDto cursorInfoDto, SortedCallback callback){
        String sortField = request.getSortField();
        String sortDirection = request.getSortDirection();

        validateSortParameters(sortField, sortDirection);
        boolean isDesc = "desc".equalsIgnoreCase(sortDirection);

        String fieldCursor = cursorInfoDto.getFieldCursor();
        Long idCursor = cursorInfoDto.getIdCursor();

        List<IndexInfo> result = callback.call(request.getIndexClassification(), request.getIndexName(), request.getFavorite(), fieldCursor, idCursor);

        if ("indexName".equals(sortField)) {
            result.sort(Comparator.comparingInt(i -> extractNumber(i.getIndexName())));
            if (isDesc) {
                Collections.reverse(result);
            }
        }

        return result.stream().limit(request.getSize()).collect(Collectors.toList());
    }

    private void validateSortParameters(String sortField, String sortDirection) {
        List<String> allowedFields = List.of("indexClassification", "indexName");
        if (!allowedFields.contains(sortField)) {
            throw new IllegalArgumentException("Invalid sort field: " + sortField);
        }
        if (!List.of("asc", "desc").contains(sortDirection.toLowerCase())) {
            throw new IllegalArgumentException("Invalid sort direction: " + sortDirection);
        }
    }

    private int extractNumber(String str) {
        Matcher matcher = Pattern.compile("\\d+").matcher(str);
        return matcher.find() ? Integer.parseInt(matcher.group()) : 0;
    }
}
