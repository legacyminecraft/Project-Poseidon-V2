package com.legacyminecraft.poseidon.profile;

import org.jspecify.annotations.Nullable;

import java.util.UUID;

public final class PlayerProfileImpl implements PlayerProfile {

    private MinecraftProfile profile;

    public PlayerProfileImpl(MinecraftProfile profile) {
        this.profile = profile;
    }

    @Override
    public @Nullable String getName() {
        return this.profile.name();
    }

    @Override
    public UUID getUniqueId() {
        return this.profile.id();
    }
}
