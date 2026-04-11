package com.sgu.admission_desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.dto.MajorSubjectGroup.ListMajorSubjectGroupCreationRequest;
import com.sgu.admission_desktop.dto.MajorSubjectGroup.MajorSubjectGroupCreationRequest;
import com.sgu.admission_desktop.dto.MajorSubjectGroup.MajorSubjectGroupResponse;
import com.sgu.admission_desktop.dto.MajorSubjectGroup.MajorSubjectGroupUpdateRequest;
import com.sgu.admission_desktop.util.URLUtil;

import java.util.List;

public class MajorSubjectGroupService extends BaseApiService {

    public ApiResponse<List<MajorSubjectGroupResponse>> getAll() {
        return get(
                URLUtil.MAJOR_SUBJECT_GROUP.GET_ALL,
                true,
                new TypeReference<ApiResponse<List<MajorSubjectGroupResponse>>>() {
                }
        );
    }

    public ApiResponse<MajorSubjectGroupResponse> getById(int id) {
        return get(
                URLUtil.MAJOR_SUBJECT_GROUP.GET_BY_ID(id),
                true,
                new TypeReference<ApiResponse<MajorSubjectGroupResponse>>() {
                }
        );
    }

    public ApiResponse<MajorSubjectGroupResponse> create(MajorSubjectGroupCreationRequest request) {
        return post(
                URLUtil.MAJOR_SUBJECT_GROUP.CREATE,
                request,
                true,
                new TypeReference<ApiResponse<MajorSubjectGroupResponse>>() {
                }
        );
    }

    public ApiResponse<List<MajorSubjectGroupResponse>> createBulk(ListMajorSubjectGroupCreationRequest request) {
        return post(
                URLUtil.MAJOR_SUBJECT_GROUP.CREATE_BULK,
                request,
                true,
                new TypeReference<ApiResponse<List<MajorSubjectGroupResponse>>>() {
                }
        );
    }

    public ApiResponse<MajorSubjectGroupResponse> update(int id, MajorSubjectGroupUpdateRequest request) {
        return put(
                URLUtil.MAJOR_SUBJECT_GROUP.UPDATE(id),
                request,
                true,
                new TypeReference<ApiResponse<MajorSubjectGroupResponse>>() {
                }
        );
    }

    public ApiResponse<Void> delete(int id) {
        return delete(
                URLUtil.MAJOR_SUBJECT_GROUP.DELETE(id),
                true,
                new TypeReference<ApiResponse<Void>>() {
                }
        );
    }
}
