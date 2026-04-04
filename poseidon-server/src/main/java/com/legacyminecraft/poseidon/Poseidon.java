package com.legacyminecraft.poseidon;

import com.legacyminecraft.poseidon.config.PoseidonConfig;
import com.legacyminecraft.poseidon.profile.ProfileCache;
import com.legacyminecraft.poseidon.profile.ProfileService;
import com.legacyminecraft.poseidon.service.ServiceClient;

public final class Poseidon {

    private Poseidon() {
    }

    public static PoseidonConfig config() {
        return PoseidonConfig.getInstance();
    }

    public static ProfileCache getProfileCache() {
        return ProfileCache.getInstance();
    }

    public static ServiceClient getServiceClient() {
        return ServiceClient.getInstance();
    }

    public static ProfileService getProfileService() {
        return ProfileService.getInstance();
    }
}
