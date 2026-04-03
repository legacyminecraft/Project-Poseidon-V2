package com.legacyminecraft.poseidon.profile;

public class ProfileLookupException extends Exception {

    private final ErrorType errorType;

    public ProfileLookupException(ErrorType errorType) {
        super();
        this.errorType = errorType;
    }

    public ProfileLookupException(ErrorType errorType, Throwable cause) {
        super(cause);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return this.errorType;
    }

    public enum ErrorType {
        SERVICE_UNREACHABLE,
        HTTP_ERROR,
        BAD_RESPONSE,
        PROFILE_NOT_FOUND
    }
}
