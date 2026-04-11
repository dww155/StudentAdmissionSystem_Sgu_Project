package com.sgu.admission_desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.dto.Authentication.IntrospectRequest;
import com.sgu.admission_desktop.dto.Authentication.IntrospectResponse;
import com.sgu.admission_desktop.dto.Authentication.LoginRequest;
import com.sgu.admission_desktop.dto.Authentication.LoginResponse;
import com.sgu.admission_desktop.util.URLUtil;

public class AuthService extends BaseApiService {

    public ApiResponse<LoginResponse> login(LoginRequest request) {
        return post(
                URLUtil.AUTH.LOGIN,
                request,
                false,
                new TypeReference<ApiResponse<LoginResponse>>() {
                }
        );
    }

    public ApiResponse<IntrospectResponse> introspect(IntrospectRequest request) {
        return post(
                URLUtil.AUTH.INTROSPECT,
                request,
                false,
                new TypeReference<ApiResponse<IntrospectResponse>>() {
                }
        );
    }
}
