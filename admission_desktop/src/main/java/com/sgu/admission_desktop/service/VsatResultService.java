package com.sgu.admission_desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.dto.VsatResult.ListVsatResultCreationRequest;
import com.sgu.admission_desktop.dto.VsatResult.VsatResultCreationRequest;
import com.sgu.admission_desktop.dto.VsatResult.VsatResultResponse;
import com.sgu.admission_desktop.dto.VsatResult.VsatResultUpdateRequest;
import com.sgu.admission_desktop.util.URLUtil;

import java.util.List;
import java.util.Map;

public class VsatResultService extends BaseApiService {

    public ApiResponse<List<VsatResultResponse>> getAll() {
        return get(
                URLUtil.VSAT_RESULT.GET_ALL,
                true,
                new TypeReference<ApiResponse<List<VsatResultResponse>>>() {
                }
        );
    }

    public ApiResponse<Map<String, Object>> getPaginated(int page, int size, String sortBy, String sortDir) {
        return get(
                URLUtil.VSAT_RESULT.GET_PAGINATED(page, size, sortBy, sortDir),
                true,
                new TypeReference<ApiResponse<Map<String, Object>>>() {
                }
        );
    }

    public ApiResponse<VsatResultResponse> getById(long id) {
        return get(
                URLUtil.VSAT_RESULT.GET_BY_ID(id),
                true,
                new TypeReference<ApiResponse<VsatResultResponse>>() {
                }
        );
    }

    public ApiResponse<VsatResultResponse> getByCccd(String cccd) {
        return get(
                URLUtil.VSAT_RESULT.GET_BY_CCCD(cccd),
                true,
                new TypeReference<ApiResponse<VsatResultResponse>>() {
                }
        );
    }

    public ApiResponse<VsatResultResponse> create(VsatResultCreationRequest request) {
        return post(
                URLUtil.VSAT_RESULT.CREATE,
                request,
                true,
                new TypeReference<ApiResponse<VsatResultResponse>>() {
                }
        );
    }

    public ApiResponse<List<VsatResultResponse>> createBulk(ListVsatResultCreationRequest request) {
        return post(
                URLUtil.VSAT_RESULT.CREATE_BULK,
                request,
                true,
                new TypeReference<ApiResponse<List<VsatResultResponse>>>() {
                }
        );
    }

    public ApiResponse<VsatResultResponse> update(long id, VsatResultUpdateRequest request) {
        return put(
                URLUtil.VSAT_RESULT.UPDATE(id),
                request,
                true,
                new TypeReference<ApiResponse<VsatResultResponse>>() {
                }
        );
    }

    public ApiResponse<Void> delete(long id) {
        return delete(
                URLUtil.VSAT_RESULT.DELETE(id),
                true,
                new TypeReference<ApiResponse<Void>>() {
                }
        );
    }
}
