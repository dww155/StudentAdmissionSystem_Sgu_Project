package com.sgu.admission_desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sgu.admission_desktop.dto.AdmissionBonusScore.AdmissionBonusScoreCreationRequest;
import com.sgu.admission_desktop.dto.AdmissionBonusScore.AdmissionBonusScoreResponse;
import com.sgu.admission_desktop.dto.AdmissionBonusScore.AdmissionBonusScoreUpdateRequest;
import com.sgu.admission_desktop.dto.AdmissionBonusScore.ListAdmissionBonusScoreCreationRequest;
import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.util.URLUtil;

import java.util.List;

public class AdmissionBonusScoreService extends BaseApiService {

    public ApiResponse<List<AdmissionBonusScoreResponse>> getAll() {
        return get(
                URLUtil.ADMISSION_BONUS_SCORE.GET_ALL,
                true,
                new TypeReference<ApiResponse<List<AdmissionBonusScoreResponse>>>() {
                }
        );
    }

    public ApiResponse<AdmissionBonusScoreResponse> getById(int id) {
        return get(
                URLUtil.ADMISSION_BONUS_SCORE.GET_BY_ID(id),
                true,
                new TypeReference<ApiResponse<AdmissionBonusScoreResponse>>() {
                }
        );
    }

    public ApiResponse<AdmissionBonusScoreResponse> create(AdmissionBonusScoreCreationRequest request) {
        return post(
                URLUtil.ADMISSION_BONUS_SCORE.CREATE,
                request,
                true,
                new TypeReference<ApiResponse<AdmissionBonusScoreResponse>>() {
                }
        );
    }

    public ApiResponse<List<AdmissionBonusScoreResponse>> createBulk(ListAdmissionBonusScoreCreationRequest request) {
        return post(
                URLUtil.ADMISSION_BONUS_SCORE.CREATE_BULK,
                request,
                true,
                new TypeReference<ApiResponse<List<AdmissionBonusScoreResponse>>>() {
                }
        );
    }

    public ApiResponse<AdmissionBonusScoreResponse> update(int id, AdmissionBonusScoreUpdateRequest request) {
        return put(
                URLUtil.ADMISSION_BONUS_SCORE.UPDATE(id),
                request,
                true,
                new TypeReference<ApiResponse<AdmissionBonusScoreResponse>>() {
                }
        );
    }

    public ApiResponse<Void> delete(int id) {
        return delete(
                URLUtil.ADMISSION_BONUS_SCORE.DELETE(id),
                true,
                new TypeReference<ApiResponse<Void>>() {
                }
        );
    }
}
