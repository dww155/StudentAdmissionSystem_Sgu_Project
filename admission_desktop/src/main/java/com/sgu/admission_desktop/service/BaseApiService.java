package com.sgu.admission_desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sgu.admission_desktop.util.ApiClient;
import com.sgu.admission_desktop.util.ObjectMapperUtil;

import java.io.IOException;

abstract class BaseApiService {

    protected <T> T get(String url, boolean withAuth, TypeReference<T> typeRef) {
        return execute(() -> ApiClient.get(url, withAuth), typeRef);
    }

    protected <T> T post(String url, Object request, boolean withAuth, TypeReference<T> typeRef) {
        return execute(() -> ApiClient.post(url, toJson(request), withAuth), typeRef);
    }

    protected <T> T put(String url, Object request, boolean withAuth, TypeReference<T> typeRef) {
        return execute(() -> ApiClient.put(url, toJson(request), withAuth), typeRef);
    }

    protected <T> T delete(String url, boolean withAuth, TypeReference<T> typeRef) {
        return execute(() -> ApiClient.delete(url, withAuth), typeRef);
    }

    private String toJson(Object payload) {
        try {
            return ObjectMapperUtil.OBJECT_MAPPER.writeValueAsString(payload);
        } catch (IOException e) {
            throw new RuntimeException("Cannot serialize request payload", e);
        }
    }

    private <T> T execute(CheckedRequest request, TypeReference<T> typeRef) {
        try {
            String responseBody = request.call();
            return ObjectMapperUtil.OBJECT_MAPPER.readValue(responseBody, typeRef);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("HTTP request was interrupted", e);
        } catch (IOException e) {
            throw new RuntimeException("Cannot process HTTP response", e);
        }
    }

    @FunctionalInterface
    private interface CheckedRequest {
        String call() throws IOException, InterruptedException;
    }
}
