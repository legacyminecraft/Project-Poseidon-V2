package com.legacyminecraft.poseidon.profile;

public class ProfileNotFoundException extends ProfileLookupException {

    public ProfileNotFoundException() {
        super(ErrorType.PROFILE_NOT_FOUND);
    }
}
