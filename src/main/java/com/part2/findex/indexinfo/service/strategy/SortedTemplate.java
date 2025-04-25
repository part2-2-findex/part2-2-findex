package com.part2.findex.indexinfo.service.strategy;

import com.part2.findex.indexinfo.dto.CursorInfoDto;
import com.part2.findex.indexinfo.dto.request.IndexSearchRequest;
import com.part2.findex.indexinfo.entity.IndexInfo;
import com.part2.findex.indexinfo.repository.springjpa.SpringDataIndexInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SortedTemplate {
    private final SpringDataIndexInfoRepository indexInfoRepository;

    public List<IndexInfo> execute(IndexSearchRequest request, CursorInfoDto cursorInfoDto, SortedCallback callback) {
        String sortField = request.getSortField();
        String sortDirection = request.getSortDirection();

        // Service 단에서 LIKE 파라미터 준비
        String indexClassification = prepareLikeParam(request.getIndexClassification());
        String indexName = prepareLikeParam(request.getIndexName());

        validateSortParameters(sortField, sortDirection);

        String fieldCursor = cursorInfoDto.getFieldCursor();
        Long idCursor = cursorInfoDto.getIdCursor();

        // DB 정렬 결과 그대로 사용
        List<IndexInfo> result = callback.call(
                indexClassification,
                indexName,
                request.getFavorite(),
                fieldCursor,
                idCursor
        );

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

    // LIKE 파라미터 준비
    private String prepareLikeParam(String param) {
        if (param == null || param.trim().isEmpty()) {
            return "%";  // 모든 값 포함
        }
        return "%" + param.trim() + "%";
    }
}
