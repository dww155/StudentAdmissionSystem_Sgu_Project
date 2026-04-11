package com.sgu.admission_desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.dto.Major.ListMajorCreationRequest;
import com.sgu.admission_desktop.dto.Major.MajorCreationRequest;
import com.sgu.admission_desktop.dto.Major.MajorResponse;
import com.sgu.admission_desktop.dto.Major.MajorUpdateRequest;
import com.sgu.admission_desktop.util.URLUtil;

import java.util.List;

public class MajorService extends BaseApiService {

    public ApiResponse<List<MajorResponse>> getAll() {
        return get(
                URLUtil.MAJOR.GET_ALL,
                true,
                new TypeReference<ApiResponse<List<MajorResponse>>>() {
                }
        );
    }

    public ApiResponse<MajorResponse> getById(int id) {
        return get(
                URLUtil.MAJOR.GET_BY_ID(id),
                true,
                new TypeReference<ApiResponse<MajorResponse>>() {
                }
        );
    }

    public ApiResponse<MajorResponse> create(MajorCreationRequest request) {
        return post(
                URLUtil.MAJOR.CREATE,
                request,
                true,
                new TypeReference<ApiResponse<MajorResponse>>() {
                }
        );
    }

    public ApiResponse<List<MajorResponse>> createBulk(ListMajorCreationRequest request) {
        return post(
                URLUtil.MAJOR.CREATE_BULK,
                request,
                true,
                new TypeReference<ApiResponse<List<MajorResponse>>>() {
                }
        );
    }

    public ApiResponse<MajorResponse> update(int id, MajorUpdateRequest request) {
        return put(
                URLUtil.MAJOR.UPDATE(id),
                request,
                true,
                new TypeReference<ApiResponse<MajorResponse>>() {
                }
        );
    }

    public ApiResponse<Void> delete(int id) {
        return delete(
                URLUtil.MAJOR.DELETE(id),
                true,
                new TypeReference<ApiResponse<Void>>() {
                }
        );
    }
}
