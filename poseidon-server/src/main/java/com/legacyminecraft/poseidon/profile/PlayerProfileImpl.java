package com.legacyminecraft.poseidon.profile;

import com.google.common.base.MoreObjects;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public final class PlayerProfileImpl implements PlayerProfile {

    private MinecraftProfile profile;

    public PlayerProfileImpl(MinecraftProfile profile) {
        this.profile = profile;
    }

    @Override
    public @Nullable String getName() {
        return this.profile.name().isEmpty() ? null : this.profile.name();
    }

    @Override
    public UUID getUniqueId() {
        return this.profile.id();
    }

    @Override
    public boolean isOnlineMode() {
        return this.profile.onlineMode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("profile", this.profile)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PlayerProfileImpl that)) {
            return false;
        }
        return Objects.equals(this.profile, that.profile);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.profile);
    }
}
