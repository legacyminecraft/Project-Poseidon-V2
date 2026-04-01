package com.legacyminecraft.poseidon;

import com.legacyminecraft.poseidon.config.PoseidonConfig;
import com.legacyminecraft.poseidon.profile.ProfileCache;

public final class Poseidon {

    private Poseidon() {
    }

    public static PoseidonConfig config() {
        return PoseidonConfig.getInstance();
    }

    public static ProfileCache getProfileCache() {
        return ProfileCache.getInstance();
    }
}
