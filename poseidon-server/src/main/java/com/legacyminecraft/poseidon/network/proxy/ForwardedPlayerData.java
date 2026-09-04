package com.legacyminecraft.poseidon.network.proxy;

import com.legacyminecraft.poseidon.profile.MinecraftProfile;

import java.net.InetAddress;

public record ForwardedPlayerData(InetAddress address, MinecraftProfile profile) {
}
