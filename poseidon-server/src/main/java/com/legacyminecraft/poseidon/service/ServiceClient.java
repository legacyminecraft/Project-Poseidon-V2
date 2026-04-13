package com.legacyminecraft.poseidon.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.legacyminecraft.poseidon.profile.MinecraftProfile;
import com.legacyminecraft.poseidon.profile.MinecraftProfileDeserializer;
import com.legacyminecraft.poseidon.profile.ProfileBulkLookupResponse;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class ServiceClient {

    private static final Duration REQUEST_TIMEOUT = Duration.ofMillis(5000);

    private final HttpClient client;
    private final Gson gson;

    public ServiceClient() {
        this.client = HttpClient.newBuilder().connectTimeout(REQUEST_TIMEOUT).build();
        this.gson = new GsonBuilder()
                .disableHtmlEscaping()
                .registerTypeAdapter(MinecraftProfile.class, new MinecraftProfileDeserializer())
                .registerTypeAdapter(ProfileBulkLookupResponse.class, new ProfileBulkLookupResponse.Deserializer())
                .create();
    }

    public <T> @Nullable T get(String url, Class<T> responseClass) throws ServiceClientException {
        HttpRequest request;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("Content-Type", "application/json")
                    .GET()
                    .timeout(REQUEST_TIMEOUT)
                    .build();
        } catch (Throwable e) {
            throw new ServiceClientException(ServiceClientException.ErrorType.BAD_REQUEST, e);
        }

        return sendRequest(request, responseClass);
    }

    public <T> @Nullable T post(String url, HttpRequest.BodyPublisher bodyPublisher, Class<T> responseClass) throws ServiceClientException {
        HttpRequest request;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("Content-Type", "application/json")
                    .POST(bodyPublisher)
                    .timeout(REQUEST_TIMEOUT)
                    .build();
        } catch (Throwable e) {
            throw new ServiceClientException(ServiceClientException.ErrorType.BAD_REQUEST, e);
        }

        return sendRequest(request, responseClass);
    }

    private <T> @Nullable T sendRequest(HttpRequest request, Class<T> responseClass) throws ServiceClientException {
        HttpResponse<String> response;
        try {
            response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Throwable e) {
            throw new ServiceClientException(ServiceClientException.ErrorType.SERVICE_UNREACHABLE, e);
        }

        int status = response.statusCode();
        if (status < 400) {
            try {
                return response.body().isEmpty() ? null : gson.fromJson(response.body(), responseClass);
            } catch (JsonParseException e) {
                throw new ServiceClientException(ServiceClientException.ErrorType.BAD_RESPONSE, e);
            }
        } else {
            throw new ServiceClientHttpException(response, "Service at " + request.uri() + " returned response code " + response.statusCode());
        }
    }
}
