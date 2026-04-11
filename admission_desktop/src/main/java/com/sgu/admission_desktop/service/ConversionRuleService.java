package com.sgu.admission_desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.dto.ConversionRule.ConversionRuleCreationRequest;
import com.sgu.admission_desktop.dto.ConversionRule.ConversionRuleResponse;
import com.sgu.admission_desktop.dto.ConversionRule.ConversionRuleUpdateRequest;
import com.sgu.admission_desktop.dto.ConversionRule.ListConversionRuleCreationRequest;
import com.sgu.admission_desktop.util.URLUtil;

import java.util.List;

public class ConversionRuleService extends BaseApiService {

    public ApiResponse<List<ConversionRuleResponse>> getAll() {
        return get(
                URLUtil.CONVERSION_RULE.GET_ALL,
                true,
                new TypeReference<ApiResponse<List<ConversionRuleResponse>>>() {
                }
        );
    }

    public ApiResponse<ConversionRuleResponse> getById(int id) {
        return get(
                URLUtil.CONVERSION_RULE.GET_BY_ID(id),
                true,
                new TypeReference<ApiResponse<ConversionRuleResponse>>() {
                }
        );
    }

    public ApiResponse<ConversionRuleResponse> create(ConversionRuleCreationRequest request) {
        return post(
                URLUtil.CONVERSION_RULE.CREATE,
                request,
                true,
                new TypeReference<ApiResponse<ConversionRuleResponse>>() {
                }
        );
    }

    public ApiResponse<List<ConversionRuleResponse>> createBulk(ListConversionRuleCreationRequest request) {
        return post(
                URLUtil.CONVERSION_RULE.CREATE_BULK,
                request,
                true,
                new TypeReference<ApiResponse<List<ConversionRuleResponse>>>() {
                }
        );
    }

    public ApiResponse<ConversionRuleResponse> update(int id, ConversionRuleUpdateRequest request) {
        return put(
                URLUtil.CONVERSION_RULE.UPDATE(id),
                request,
                true,
                new TypeReference<ApiResponse<ConversionRuleResponse>>() {
                }
        );
    }

    public ApiResponse<Void> delete(int id) {
        return delete(
                URLUtil.CONVERSION_RULE.DELETE(id),
                true,
                new TypeReference<ApiResponse<Void>>() {
                }
        );
    }
}
