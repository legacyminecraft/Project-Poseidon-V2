package com.legacyminecraft.poseidon.profile;

import java.net.http.HttpResponse;

public class ProfileLookupHttpException extends ProfileLookupException {

    private final HttpResponse<String> response;

    public ProfileLookupHttpException(HttpResponse<String> response) {
        super(ErrorType.HTTP_ERROR);
        this.response = response;
    }

    public HttpResponse<String> getResponse() {
        return this.response;
    }
}
