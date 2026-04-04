package com.legacyminecraft.poseidon.service;

import java.net.http.HttpResponse;

public class ServiceClientHttpException extends ServiceClientException {

    private final HttpResponse<String> response;

    public ServiceClientHttpException(HttpResponse<String> response) {
        super(ErrorType.HTTP_ERROR);
        this.response = response;
    }

    public HttpResponse<String> getResponse() {
        return this.response;
    }
}
