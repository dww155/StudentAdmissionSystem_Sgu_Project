package com.sgu.admission_desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.dto.EnglishCertification.EnglishCertificationCreationRequest;
import com.sgu.admission_desktop.dto.EnglishCertification.EnglishCertificationResponse;
import com.sgu.admission_desktop.dto.EnglishCertification.EnglishCertificationUpdateRequest;
import com.sgu.admission_desktop.dto.EnglishCertification.ListEnglishCertificationCreationRequest;
import com.sgu.admission_desktop.util.URLUtil;

import java.util.List;
import java.util.Map;

public class EnglishCertificationService extends BaseApiService {

    public ApiResponse<List<EnglishCertificationResponse>> getAll() {
        return get(
                URLUtil.ENGLISH_CERTIFICATION.GET_ALL,
                true,
                new TypeReference<ApiResponse<List<EnglishCertificationResponse>>>() {
                }
        );
    }

    public ApiResponse<Map<String, Object>> getPaginated(int page, int size, String sortBy, String sortDir) {
        return get(
                URLUtil.ENGLISH_CERTIFICATION.GET_PAGINATED(page, size, sortBy, sortDir),
                true,
                new TypeReference<ApiResponse<Map<String, Object>>>() {
                }
        );
    }

    public ApiResponse<EnglishCertificationResponse> getById(long id) {
        return get(
                URLUtil.ENGLISH_CERTIFICATION.GET_BY_ID(id),
                true,
                new TypeReference<ApiResponse<EnglishCertificationResponse>>() {
                }
        );
    }

    public ApiResponse<EnglishCertificationResponse> getByCccd(String cccd) {
        return get(
                URLUtil.ENGLISH_CERTIFICATION.GET_BY_CCCD(cccd),
                true,
                new TypeReference<ApiResponse<EnglishCertificationResponse>>() {
                }
        );
    }

    public ApiResponse<EnglishCertificationResponse> create(EnglishCertificationCreationRequest request) {
        return post(
                URLUtil.ENGLISH_CERTIFICATION.CREATE,
                request,
                true,
                new TypeReference<ApiResponse<EnglishCertificationResponse>>() {
                }
        );
    }

    public ApiResponse<List<EnglishCertificationResponse>> createBulk(ListEnglishCertificationCreationRequest request) {
        return post(
                URLUtil.ENGLISH_CERTIFICATION.CREATE_BULK,
                request,
                true,
                new TypeReference<ApiResponse<List<EnglishCertificationResponse>>>() {
                }
        );
    }

    public ApiResponse<EnglishCertificationResponse> update(long id, EnglishCertificationUpdateRequest request) {
        return put(
                URLUtil.ENGLISH_CERTIFICATION.UPDATE(id),
                request,
                true,
                new TypeReference<ApiResponse<EnglishCertificationResponse>>() {
                }
        );
    }

    public ApiResponse<Void> delete(long id) {
        return delete(
                URLUtil.ENGLISH_CERTIFICATION.DELETE(id),
                true,
                new TypeReference<ApiResponse<Void>>() {
                }
        );
    }
}
