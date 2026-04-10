package com.sgu.admission_desktop.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.dto.Authentication.LoginRequest;
import com.sgu.admission_desktop.dto.Authentication.LoginResponse;
import lombok.Setter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiClient {

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // ===================== SET TOKEN =====================
    @Setter
    private static String token; // JWT

    private static final String APPLICATION_JSON = "application/json";

    public static void clearToken() {
        token = null;
    }

    // ===================== ADD JWT HEADER =====================
    private static void addAuthHeader(HttpRequest.Builder builder) {
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }
    }

    // ===================== GET =====================
    public static String get(String url, boolean withAuth) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", APPLICATION_JSON);

        if (withAuth) {
            addAuthHeader(builder);
        }

        return sendRequest(builder.build());
    }

    // overload (mặc định có auth)
    public static String get(String url) throws IOException, InterruptedException {
        return get(url, true);
    }

    // ===================== POST =====================
    public static String post(String url, String jsonBody, boolean withAuth) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", APPLICATION_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody));

        if (withAuth) {
            addAuthHeader(builder);
        }

        return sendRequest(builder.build());
    }

    // overload
    public static String post(String url, String jsonBody) throws IOException, InterruptedException {
        return post(url, jsonBody, true);
    }

    // ===================== PUT =====================
    public static String put(String url, String jsonBody, boolean withAuth) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", APPLICATION_JSON)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody));

        if (withAuth) {
            addAuthHeader(builder);
        }

        return sendRequest(builder.build());
    }

    // overload
    public static String put(String url, String jsonBody) throws IOException, InterruptedException {
        return put(url, jsonBody, true);
    }

    // ===================== DELETE =====================
    public static String delete(String url, boolean withAuth) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .header("Accept", APPLICATION_JSON);

        if (withAuth) {
            addAuthHeader(builder);
        }

        return sendRequest(builder.build());
    }

    // overload
    public static String delete(String url) throws IOException, InterruptedException {
        return delete(url, true);
    }

    // ===================== CORE =====================
    private static String sendRequest(HttpRequest request) throws IOException, InterruptedException {

        // DEBUG REQUEST
        System.out.println("=== HTTP REQUEST ===");
        System.out.println("URI: " + request.uri());
        System.out.println("Method: " + request.method());
        System.out.println("Headers: " + request.headers().map());

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        // DEBUG RESPONSE
        System.out.println("=== HTTP RESPONSE ===");
        System.out.println("Status: " + response.statusCode());
        System.out.println("Body: " + response.body());

        int statusCode = response.statusCode();

        if (statusCode >= 200 && statusCode < 300) {
            return response.body();
        } else {
            throw new RuntimeException(
                    "HTTP Error: " + statusCode + " - " + response.body()
            );
        }
    }

    // ===================== TEST MAIN =====================
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        try {
            // ===================== LOGIN =====================
            LoginRequest request = LoginRequest.builder()
                    .username("admin")
                    .password("Asdf1234!")
                    .build();

            String json = objectMapper.writeValueAsString(request);
            System.out.println(json);

            String response = ApiClient.post(
                    URLUtil.AUTH.LOGIN,
                    json,
                    false // ❌ login không cần token
            );

            ApiResponse<LoginResponse> apiResponse = objectMapper.readValue(
                    response,
                    new TypeReference<ApiResponse<LoginResponse>>() {}
            );
            // ===================== GET TOKEN =====================
            String token = apiResponse.getData().getToken();

            if (token != null) {
                System.out.println("JWT Token: " + token);

                // lưu token
                ApiClient.setToken(token);

                // ===================== TEST API AUTH =====================
                String userInfo = ApiClient.get(
                        URLUtil.USER.GET_INFO,
                        true // ✅ cần token
                );

                System.out.println("User info:");
                System.out.println(userInfo);
            } else {
                System.out.println("Không lấy được token!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}