package com.part2.findex.indexdata.service.strategy;

import com.part2.findex.indexdata.dto.request.IndexDataRequest;
import com.part2.findex.indexdata.entity.IndexData;
import com.part2.findex.indexinfo.dto.CursorInfoDto;
import com.part2.findex.indexinfo.repository.springjpa.SpringDataIndexInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component("indexDataSortedTemplate")
@RequiredArgsConstructor
public class SortedTemplate {
    private final SpringDataIndexInfoRepository indexInfoRepository;

    public <T> List<IndexData> execute(IndexDataRequest request, CursorInfoDto cursorInfoDto, SortedCallback<T> callback) {
        String sortField = request.getSortField();
        String sortDirection = request.getSortDirection();
        validateSortParameters(sortField, sortDirection);

        Long indexInfoId = request.getIndexInfoId();
        LocalDate startDate = request.getStartDate() != null
                ? LocalDate.parse(request.getStartDate())
                : LocalDate.of(1900, 1, 1);

        LocalDate endDate = request.getEndDate() != null
                ? LocalDate.parse(request.getEndDate())
                : LocalDate.of(9999, 12, 31);


        T baseDateCursor;

        if ("baseDate".equalsIgnoreCase(sortField)) {
            baseDateCursor = (T) baseDateCursorParser(sortDirection, cursorInfoDto);
        } else {
            baseDateCursor = (T) baseDataCursorParser(sortDirection, cursorInfoDto);
        }

        Long idCursor = cursorInfoDto.getIdCursor();

        // DB 정렬 결과 그대로 사용
        List<IndexData> result = callback.call(
                indexInfoId,
                startDate,
                endDate,
                baseDateCursor,
                idCursor
        );

        // 제한된 개수만 반환 (페이징)
        return result.stream().limit(request.getSize()).collect(Collectors.toList());
    }

    private LocalDate baseDateCursorParser(String sortDirection, CursorInfoDto cursorInfoDto) {
        LocalDate baseDateCursor = null;

        if ("desc".equals(sortDirection)) baseDateCursor = cursorInfoDto.getFieldCursor() != null
                ? LocalDate.parse(cursorInfoDto.getFieldCursor())
                : LocalDate.of(9999, 12, 31);
        else {
            baseDateCursor = cursorInfoDto.getFieldCursor() != null
                    ? LocalDate.parse(cursorInfoDto.getFieldCursor())
                    : LocalDate.of(1900, 1, 1);
        }

        return baseDateCursor;
    }

    private Double baseDataCursorParser(String sortDirection, CursorInfoDto cursorInfoDto) {
        Double closingPriceCursor;

        if ("desc".equals(sortDirection)) {
            closingPriceCursor = cursorInfoDto.getFieldCursor() != null
                    ? Double.valueOf(cursorInfoDto.getFieldCursor())
                    : Double.MAX_VALUE; // Long 대신 Double 사용
        } else {
            closingPriceCursor = cursorInfoDto.getFieldCursor() != null
                    ? Double.valueOf(cursorInfoDto.getFieldCursor())
                    : 0.0; // 최소값으로 0.0 사용 (가격은 음수가 아닐 것)
        }

        return closingPriceCursor;
    }

    private void validateSortParameters(String sortField, String sortDirection) {
        // 정렬 기준 필드와 방향 검증
        List<String> allowedFields = List.of("baseDate", "closingPrice"); // 수정된 정렬 기준
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
