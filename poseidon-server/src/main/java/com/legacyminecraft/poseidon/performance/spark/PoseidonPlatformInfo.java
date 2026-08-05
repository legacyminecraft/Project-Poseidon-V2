package com.legacyminecraft.poseidon.performance.spark;

import me.lucko.spark.common.platform.PlatformInfo;
import org.bukkit.Server;

public final class PoseidonPlatformInfo implements PlatformInfo {

    private final Server server;

    public PoseidonPlatformInfo(Server server) {
        this.server = server;
    }

    @Override
    public Type getType() {
        return Type.SERVER;
    }

    @Override
    public String getName() {
        return getBrand();
    }

    @Override
    public String getBrand() {
        return this.server.getName();
    }

    @Override
    public String getVersion() {
        return this.server.getVersion();
    }

    @Override
    public String getMinecraftVersion() {
        return "b1.7.3";
    }
}
