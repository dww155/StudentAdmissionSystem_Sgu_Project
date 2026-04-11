package com.sgu.admission_desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sgu.admission_desktop.dto.AdmissionPreference.AdmissionPreferenceCreationRequest;
import com.sgu.admission_desktop.dto.AdmissionPreference.AdmissionPreferenceResponse;
import com.sgu.admission_desktop.dto.AdmissionPreference.AdmissionPreferenceUpdateRequest;
import com.sgu.admission_desktop.dto.AdmissionPreference.ListAdmissionPreferenceCreationRequest;
import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.util.URLUtil;

import java.util.List;

public class AdmissionPreferenceService extends BaseApiService {

    public ApiResponse<List<AdmissionPreferenceResponse>> getAll() {
        return get(
                URLUtil.ADMISSION_PREFERENCE.GET_ALL,
                true,
                new TypeReference<ApiResponse<List<AdmissionPreferenceResponse>>>() {
                }
        );
    }

    public ApiResponse<AdmissionPreferenceResponse> getById(int id) {
        return get(
                URLUtil.ADMISSION_PREFERENCE.GET_BY_ID(id),
                true,
                new TypeReference<ApiResponse<AdmissionPreferenceResponse>>() {
                }
        );
    }

    public ApiResponse<AdmissionPreferenceResponse> create(AdmissionPreferenceCreationRequest request) {
        return post(
                URLUtil.ADMISSION_PREFERENCE.CREATE,
                request,
                true,
                new TypeReference<ApiResponse<AdmissionPreferenceResponse>>() {
                }
        );
    }

    public ApiResponse<List<AdmissionPreferenceResponse>> createBulk(ListAdmissionPreferenceCreationRequest request) {
        return post(
                URLUtil.ADMISSION_PREFERENCE.CREATE_BULK,
                request,
                true,
                new TypeReference<ApiResponse<List<AdmissionPreferenceResponse>>>() {
                }
        );
    }

    public ApiResponse<AdmissionPreferenceResponse> update(int id, AdmissionPreferenceUpdateRequest request) {
        return put(
                URLUtil.ADMISSION_PREFERENCE.UPDATE(id),
                request,
                true,
                new TypeReference<ApiResponse<AdmissionPreferenceResponse>>() {
                }
        );
    }

    public ApiResponse<Void> delete(int id) {
        return delete(
                URLUtil.ADMISSION_PREFERENCE.DELETE(id),
                true,
                new TypeReference<ApiResponse<Void>>() {
                }
        );
    }
}
