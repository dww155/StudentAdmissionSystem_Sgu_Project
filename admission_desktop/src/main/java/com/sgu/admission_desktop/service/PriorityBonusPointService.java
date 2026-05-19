package com.sgu.admission_desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.dto.PriorityBonusPoint.ListPriorityBonusPointCreationRequest;
import com.sgu.admission_desktop.dto.PriorityBonusPoint.PriorityBonusPointCreationRequest;
import com.sgu.admission_desktop.dto.PriorityBonusPoint.PriorityBonusPointResponse;
import com.sgu.admission_desktop.dto.PriorityBonusPoint.PriorityBonusPointUpdateRequest;
import com.sgu.admission_desktop.util.URLUtil;

import java.util.List;
import java.util.Map;

public class PriorityBonusPointService extends BaseApiService {

    public ApiResponse<List<PriorityBonusPointResponse>> getAll() {
        return get(
                URLUtil.PRIORITY_BONUS_POINT.GET_ALL,
                true,
                new TypeReference<ApiResponse<List<PriorityBonusPointResponse>>>() {
                }
        );
    }

    public ApiResponse<Map<String, Object>> getPaginated(int page, int size, String sortBy, String sortDir) {
        return get(
                URLUtil.PRIORITY_BONUS_POINT.GET_PAGINATED(page, size, sortBy, sortDir),
                true,
                new TypeReference<ApiResponse<Map<String, Object>>>() {
                }
        );
    }

    public ApiResponse<PriorityBonusPointResponse> getById(int id) {
        return get(
                URLUtil.PRIORITY_BONUS_POINT.GET_BY_ID(id),
                true,
                new TypeReference<ApiResponse<PriorityBonusPointResponse>>() {
                }
        );
    }

    public ApiResponse<PriorityBonusPointResponse> getByCccd(String cccd) {
        return get(
                URLUtil.PRIORITY_BONUS_POINT.GET_BY_CCCD(cccd),
                true,
                new TypeReference<ApiResponse<PriorityBonusPointResponse>>() {
                }
        );
    }

    public ApiResponse<PriorityBonusPointResponse> create(PriorityBonusPointCreationRequest request) {
        return post(
                URLUtil.PRIORITY_BONUS_POINT.CREATE,
                request,
                true,
                new TypeReference<ApiResponse<PriorityBonusPointResponse>>() {
                }
        );
    }

    public ApiResponse<List<PriorityBonusPointResponse>> createBulk(ListPriorityBonusPointCreationRequest request) {
        return post(
                URLUtil.PRIORITY_BONUS_POINT.CREATE_BULK,
                request,
                true,
                new TypeReference<ApiResponse<List<PriorityBonusPointResponse>>>() {
                }
        );
    }

    public ApiResponse<PriorityBonusPointResponse> update(int id, PriorityBonusPointUpdateRequest request) {
        return put(
                URLUtil.PRIORITY_BONUS_POINT.UPDATE(id),
                request,
                true,
                new TypeReference<ApiResponse<PriorityBonusPointResponse>>() {
                }
        );
    }

    public ApiResponse<Void> delete(int id) {
        return delete(
                URLUtil.PRIORITY_BONUS_POINT.DELETE(id),
                true,
                new TypeReference<ApiResponse<Void>>() {
                }
        );
    }
}
