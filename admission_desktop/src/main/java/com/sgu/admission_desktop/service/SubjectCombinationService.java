package com.sgu.admission_desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.dto.SubjectCombination.ListSubjectCombinationCreationRequest;
import com.sgu.admission_desktop.dto.SubjectCombination.SubjectCombinationCreationRequest;
import com.sgu.admission_desktop.dto.SubjectCombination.SubjectCombinationResponse;
import com.sgu.admission_desktop.dto.SubjectCombination.SubjectCombinationUpdateRequest;
import com.sgu.admission_desktop.util.URLUtil;

import java.util.List;

public class SubjectCombinationService extends BaseApiService {

    public ApiResponse<List<SubjectCombinationResponse>> getAll() {
        return get(
                URLUtil.SUBJECT_COMBINATION.GET_ALL,
                true,
                new TypeReference<ApiResponse<List<SubjectCombinationResponse>>>() {
                }
        );
    }

    public ApiResponse<SubjectCombinationResponse> getById(int id) {
        return get(
                URLUtil.SUBJECT_COMBINATION.GET_BY_ID(id),
                true,
                new TypeReference<ApiResponse<SubjectCombinationResponse>>() {
                }
        );
    }

    public ApiResponse<SubjectCombinationResponse> create(SubjectCombinationCreationRequest request) {
        return post(
                URLUtil.SUBJECT_COMBINATION.CREATE,
                request,
                true,
                new TypeReference<ApiResponse<SubjectCombinationResponse>>() {
                }
        );
    }

    public ApiResponse<List<SubjectCombinationResponse>> createBulk(ListSubjectCombinationCreationRequest request) {
        return post(
                URLUtil.SUBJECT_COMBINATION.CREATE_BULK,
                request,
                true,
                new TypeReference<ApiResponse<List<SubjectCombinationResponse>>>() {
                }
        );
    }

    public ApiResponse<SubjectCombinationResponse> update(int id, SubjectCombinationUpdateRequest request) {
        return put(
                URLUtil.SUBJECT_COMBINATION.UPDATE(id),
                request,
                true,
                new TypeReference<ApiResponse<SubjectCombinationResponse>>() {
                }
        );
    }

    public ApiResponse<Void> delete(int id) {
        return delete(
                URLUtil.SUBJECT_COMBINATION.DELETE(id),
                true,
                new TypeReference<ApiResponse<Void>>() {
                }
        );
    }
}
