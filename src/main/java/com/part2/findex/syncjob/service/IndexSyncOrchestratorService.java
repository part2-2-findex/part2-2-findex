package com.part2.findex.syncjob.service;

import com.part2.findex.syncjob.dto.CursorPageResponseSyncJob;
import com.part2.findex.syncjob.dto.IndexDataSyncRequest;
import com.part2.findex.syncjob.dto.SyncJobQueryRequest;
import com.part2.findex.syncjob.dto.SyncJobResult;

import java.util.List;

public interface IndexSyncOrchestratorService {

    /**
     * 외부 OpenAPI에서 최신 지수 정보를 조회하고, 내부 DB와 동기화합니다.
     * <p>
     * 동작 순서:
     * <ol>
     *   <li>최신 영업일 기준 전체 지수 데이터(OpenAPI) 조회</li>
     *   <li>DB에 존재하는 전체 지수 정보 조회</li>
     *   <li>DB에 존재하는 지수 중 이미 연동된 지수 정보는 연동 기록만 반환</li>
     *   <li>DB에 존재하지만 아직 연동되지 않은 지수 정보는 연동 후 기록 반환</li>
     *   <li>DB에 존재하지 않는 새로운 지수 정보는 DB에 저장 후 연동 기록 반환</li>
     *   <li>모든 연동 결과(SyncJobResult) 리스트로 반환</li>
     * </ol>
     *
     * @return 전체 지수 정보 동기화 결과 리스트
     */
    List<SyncJobResult> synchronizeIndexInfo();

    /**
     * 지정한 기간의 지수 데이터를 외부 OpenAPI에서 조회하여 DB와 동기화합니다.
     * <p>
     * 동작 순서:
     * <ol>
     *   <li>요청받은 기간에 대해 OpenAPI에서 전체 지수 데이터 조회</li>
     *   <li>DB에 이미 존재하는 지수 데이터(SyncJob) 확인</li>
     *   <li>DB에 없는 새로운 지수 데이터만 추출</li>
     *   <li>새로운 지수 데이터를 저장하고, 해당 SyncJob 생성</li>
     *   <li>기존 SyncJob과 신규 SyncJob을 합쳐 결과 반환</li>
     * </ol>
     *
     * @param indexDataSyncRequest 동기화할 지수 데이터의 기간 및 조건 정보
     * @return 동기화 작업 결과 리스트
     */
    List<SyncJobResult> synchronizeIndexData(IndexDataSyncRequest indexDataSyncRequest);

    CursorPageResponseSyncJob getSyncJobs(SyncJobQueryRequest request);
}
