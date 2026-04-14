package com.legacyminecraft.poseidon;

import com.legacyminecraft.poseidon.performance.TickRateManager;
import com.legacyminecraft.poseidon.profile.ProfileCache;
import com.legacyminecraft.poseidon.profile.ProfileService;
import com.legacyminecraft.poseidon.service.ServiceClient;
import com.legacyminecraft.poseidon.session.SessionService;
import com.legacyminecraft.poseidon.version.PoseidonBuildInformation;
import com.legacyminecraft.poseidon.version.PoseidonUpdateNotifier;

public final class PoseidonServer {

    private final PoseidonBuildInformation buildInformation;
    private final PoseidonUpdateNotifier updateNotifier;
    private final ProfileCache profileCache;
    private final ServiceClient serviceClient;
    private final ProfileService profileService;
    private final SessionService sessionService;
    private final TickRateManager tickRateManager;

    public PoseidonServer() {
        this.buildInformation = new PoseidonBuildInformation();
        this.profileCache = new ProfileCache();
        this.serviceClient = new ServiceClient();
        this.updateNotifier = new PoseidonUpdateNotifier(this.serviceClient, this.buildInformation);
        this.profileService = new ProfileService(this.serviceClient);
        this.sessionService = new SessionService(this.serviceClient);
        this.tickRateManager = new TickRateManager();
    }

    public void initialize() {
        getBuildInformation().load();
        getProfileCache().load();
    }

    public void postInitialize() {
        getUpdateNotifier().start();
    }

    public void shutdown() {
        getProfileCache().save();
        getUpdateNotifier().shutdown();
    }

    public PoseidonBuildInformation getBuildInformation() {
        return this.buildInformation;
    }

    public PoseidonUpdateNotifier getUpdateNotifier() {
        return this.updateNotifier;
    }

    public ProfileCache getProfileCache() {
        return this.profileCache;
    }

    public ServiceClient getServiceClient() {
        return this.serviceClient;
    }

    public ProfileService getProfileService() {
        return this.profileService;
    }

    public SessionService getSessionService() {
        return this.sessionService;
    }

    public TickRateManager getTickRateManager() {
        return this.tickRateManager;
    }
}
