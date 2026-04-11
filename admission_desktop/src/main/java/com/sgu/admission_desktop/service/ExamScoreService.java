package com.sgu.admission_desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.dto.ExamScore.ExamScoreCreationRequest;
import com.sgu.admission_desktop.dto.ExamScore.ExamScoreResponse;
import com.sgu.admission_desktop.dto.ExamScore.ExamScoreUpdateRequest;
import com.sgu.admission_desktop.dto.ExamScore.ListExamScoreCreationRequest;
import com.sgu.admission_desktop.util.URLUtil;

import java.util.List;

public class ExamScoreService extends BaseApiService {

    public ApiResponse<List<ExamScoreResponse>> getAll() {
        return get(
                URLUtil.EXAM_SCORE.GET_ALL,
                true,
                new TypeReference<ApiResponse<List<ExamScoreResponse>>>() {
                }
        );
    }

    public ApiResponse<ExamScoreResponse> getById(int id) {
        return get(
                URLUtil.EXAM_SCORE.GET_BY_ID(id),
                true,
                new TypeReference<ApiResponse<ExamScoreResponse>>() {
                }
        );
    }

    public ApiResponse<ExamScoreResponse> create(ExamScoreCreationRequest request) {
        return post(
                URLUtil.EXAM_SCORE.CREATE,
                request,
                true,
                new TypeReference<ApiResponse<ExamScoreResponse>>() {
                }
        );
    }

    public ApiResponse<List<ExamScoreResponse>> createBulk(ListExamScoreCreationRequest request) {
        return post(
                URLUtil.EXAM_SCORE.CREATE_BULK,
                request,
                true,
                new TypeReference<ApiResponse<List<ExamScoreResponse>>>() {
                }
        );
    }

    public ApiResponse<ExamScoreResponse> update(int id, ExamScoreUpdateRequest request) {
        return put(
                URLUtil.EXAM_SCORE.UPDATE(id),
                request,
                true,
                new TypeReference<ApiResponse<ExamScoreResponse>>() {
                }
        );
    }

    public ApiResponse<Void> delete(int id) {
        return delete(
                URLUtil.EXAM_SCORE.DELETE(id),
                true,
                new TypeReference<ApiResponse<Void>>() {
                }
        );
    }
}
