package com.legacyminecraft.poseidon;

import com.google.common.base.Preconditions;
import com.legacyminecraft.poseidon.config.PoseidonConfig;
import com.legacyminecraft.poseidon.network.protocol.ProtocolManagerImpl;
import com.legacyminecraft.poseidon.performance.TickRateManager;
import com.legacyminecraft.poseidon.performance.WatchdogThread;
import com.legacyminecraft.poseidon.profile.ProfileCache;
import com.legacyminecraft.poseidon.profile.ProfileService;
import com.legacyminecraft.poseidon.service.ServiceClient;
import com.legacyminecraft.poseidon.session.SessionService;
import com.legacyminecraft.poseidon.version.PoseidonBuildInformation;
import com.legacyminecraft.poseidon.version.PoseidonUpdateNotifier;
import org.jspecify.annotations.Nullable;

public final class Poseidon {

    private static @Nullable PoseidonServer server;

    private Poseidon() {
    }

    public static synchronized void setServer(PoseidonServer server) {
        Preconditions.checkState(Poseidon.server == null, "cannot redefine server");
        Poseidon.server = server;
    }

    public static PoseidonConfig getConfig() {
        return PoseidonConfig.getInstance();
    }

    public static PoseidonBuildInformation getBuildInformation() {
        return server.getBuildInformation();
    }

    public static PoseidonUpdateNotifier getUpdateNotifier() {
        return server.getUpdateNotifier();
    }

    public static ProfileCache getProfileCache() {
        return server.getProfileCache();
    }

    public static ServiceClient getServiceClient() {
        return server.getServiceClient();
    }

    public static ProfileService getProfileService() {
        return server.getProfileService();
    }

    public static SessionService getSessionService() {
        return server.getSessionService();
    }

    public static TickRateManager getTickRateManager() {
        return server.getTickRateManager();
    }

    public static WatchdogThread getWatchdogThread() {
        return server.getWatchdogThread();
    }

    public static ProtocolManagerImpl getProtocolManager() {
        return server.getProtocolManager();
    }
}
