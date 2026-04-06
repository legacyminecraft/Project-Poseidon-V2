package com.legacyminecraft.poseidon.service;

public class ServiceClientException extends Exception {

    private final ErrorType errorType;

    public ServiceClientException(ErrorType errorType) {
        super();
        this.errorType = errorType;
    }

    public ServiceClientException(ErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    public ServiceClientException(ErrorType errorType, Throwable cause) {
        super(cause);
        this.errorType = errorType;
    }

    public ServiceClientException(ErrorType errorType, String message, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return this.errorType;
    }

    public enum ErrorType {
        BAD_REQUEST,
        SERVICE_UNREACHABLE,
        HTTP_ERROR,
        BAD_RESPONSE
    }
}
