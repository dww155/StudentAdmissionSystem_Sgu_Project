package com.sgu.admission_desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.dto.Applicant.ApplicantCreationRequest;
import com.sgu.admission_desktop.dto.Applicant.ApplicantResponse;
import com.sgu.admission_desktop.dto.Applicant.ApplicantUpdateRequest;
import com.sgu.admission_desktop.dto.Applicant.ListApplicantCreationRequest;
import com.sgu.admission_desktop.util.URLUtil;

import java.util.List;

public class ApplicantService extends BaseApiService {

    public ApiResponse<List<ApplicantResponse>> getAll() {
        return get(
                URLUtil.APPLICANT.GET_ALL,
                true,
                new TypeReference<ApiResponse<List<ApplicantResponse>>>() {
                }
        );
    }

    public ApiResponse<ApplicantResponse> getById(int id) {
        return get(
                URLUtil.APPLICANT.GET_BY_ID(id),
                true,
                new TypeReference<ApiResponse<ApplicantResponse>>() {
                }
        );
    }

    public ApiResponse<ApplicantResponse> create(ApplicantCreationRequest request) {
        return post(
                URLUtil.APPLICANT.CREATE,
                request,
                true,
                new TypeReference<ApiResponse<ApplicantResponse>>() {
                }
        );
    }

    public ApiResponse<List<ApplicantResponse>> createBulk(ListApplicantCreationRequest request) {
        return post(
                URLUtil.APPLICANT.CREATE_BULK,
                request,
                true,
                new TypeReference<ApiResponse<List<ApplicantResponse>>>() {
                }
        );
    }

    public ApiResponse<ApplicantResponse> update(int id, ApplicantUpdateRequest request) {
        return put(
                URLUtil.APPLICANT.UPDATE(id),
                request,
                true,
                new TypeReference<ApiResponse<ApplicantResponse>>() {
                }
        );
    }

    public ApiResponse<Void> delete(int id) {
        return delete(
                URLUtil.APPLICANT.DELETE(id),
                true,
                new TypeReference<ApiResponse<Void>>() {
                }
        );
    }
}
