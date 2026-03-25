package com.legacyminecraft.poseidon;

import com.legacyminecraft.poseidon.config.PoseidonConfig;

public final class Poseidon {

    private Poseidon() {
    }

    public static PoseidonConfig config() {
        return PoseidonConfig.getInstance();
    }
}
