package com.legacyminecraft.poseidon.profile;

public interface ProfileLookupCallback {

    void onLookupSuccess(MinecraftProfile profile);

    void onLookupFailure(ProfileLookupException cause);
}
