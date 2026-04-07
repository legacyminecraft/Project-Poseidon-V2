package com.legacyminecraft.poseidon.profile;

import java.util.UUID;

public record MinecraftProfile(UUID id, String name, boolean onlineMode) {

    public static MinecraftProfile createOffline(String name) {
        return new MinecraftProfile(UuidUtil.createOfflineUuid(name), name, false);
    }
}
