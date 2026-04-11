package com.sgu.admission_desktop.util;

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

    private static String token;

    private static final String APPLICATION_JSON = "application/json";

    public static void setToken(String jwtToken) {
        token = jwtToken;
    }

    public static void clearToken() {
        token = null;
    }

    private static void addAuthHeader(HttpRequest.Builder builder) {
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }
    }

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

    public static String get(String url) throws IOException, InterruptedException {
        return get(url, true);
    }

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

    public static String post(String url, String jsonBody) throws IOException, InterruptedException {
        return post(url, jsonBody, true);
    }

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

    public static String put(String url, String jsonBody) throws IOException, InterruptedException {
        return put(url, jsonBody, true);
    }

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

    public static String delete(String url) throws IOException, InterruptedException {
        return delete(url, true);
    }

    private static String sendRequest(HttpRequest request) throws IOException, InterruptedException {
        System.out.println("=== HTTP REQUEST ===");
        System.out.println("URI: " + request.uri());
        System.out.println("Method: " + request.method());
        System.out.println("Headers: " + request.headers().map());

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        System.out.println("=== HTTP RESPONSE ===");
        System.out.println("Status: " + response.statusCode());
        System.out.println("Body: " + response.body());

        int statusCode = response.statusCode();

        if (statusCode >= 200 && statusCode < 300) {
            return response.body();
        }

        throw new RuntimeException("HTTP Error: " + statusCode + " - " + response.body());
    }

    public static void main(String[] args) {
        System.out.println("ApiClient is ready. Use service classes in com.sgu.admission_desktop.service.");
    }
}
