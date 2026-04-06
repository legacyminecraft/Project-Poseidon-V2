package com.legacyminecraft.poseidon.profile;

import java.util.UUID;

public record MinecraftProfile(UUID id, String name, boolean onlineMode) {
}
