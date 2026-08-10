package com.legacyminecraft.poseidon.profile;

import com.legacyminecraft.poseidon.Poseidon;

import java.util.UUID;

public record MinecraftProfile(UUID id, String name, boolean online) {

    public static MinecraftProfile createOffline(String name) {
        UUID id = Poseidon.getConfig().profiles.useLegacyUuidGeneration
                ? UuidUtil.generateLegacyOfflineUuid(name)
                : UuidUtil.generateOfflineUuid(name);
        return new MinecraftProfile(id, name, false);
    }
}
