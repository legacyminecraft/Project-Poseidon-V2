package com.legacyminecraft.poseidon;

import com.legacyminecraft.poseidon.network.connection.PacketRateLimiter;
import com.legacyminecraft.poseidon.network.connection.PingCalculator;
import com.legacyminecraft.poseidon.network.protocol.ProtocolManagerImpl;
import com.legacyminecraft.poseidon.network.proxy.ProxyHelloPacketListener;
import com.legacyminecraft.poseidon.performance.TickRateManager;
import com.legacyminecraft.poseidon.performance.WatchdogThread;
import com.legacyminecraft.poseidon.profile.ProfileCache;
import com.legacyminecraft.poseidon.profile.ProfileService;
import com.legacyminecraft.poseidon.service.ServiceClient;
import com.legacyminecraft.poseidon.session.SessionService;
import com.legacyminecraft.poseidon.util.InternalBukkitAccess;
import com.legacyminecraft.poseidon.version.PoseidonBuildInformation;
import com.legacyminecraft.poseidon.version.PoseidonUpdateNotifier;
import org.bukkit.Bukkit;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.permissions.DefaultPermissions;

public final class PoseidonServer {

    private final PoseidonBuildInformation buildInformation;
    private final PoseidonUpdateNotifier updateNotifier;
    private final ProfileCache profileCache;
    private final ServiceClient serviceClient;
    private final ProfileService profileService;
    private final SessionService sessionService;
    private final TickRateManager tickRateManager;
    private final WatchdogThread watchdogThread;
    private final ProtocolManagerImpl protocolManager;

    public PoseidonServer() {
        this.buildInformation = new PoseidonBuildInformation();
        this.profileCache = new ProfileCache();
        this.serviceClient = new ServiceClient();
        this.updateNotifier = new PoseidonUpdateNotifier(this.serviceClient, this.buildInformation);
        this.profileService = new ProfileService(this.serviceClient);
        this.sessionService = new SessionService(this.serviceClient);
        this.tickRateManager = new TickRateManager();
        this.watchdogThread = new WatchdogThread(Thread.currentThread());
        this.protocolManager = new ProtocolManagerImpl();
    }

    public void initialize() {
        getBuildInformation().load();
        getProfileCache().load();
        getProtocolManager().registerDefaults();
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        pluginManager.registerEvents(PacketRateLimiter.LISTENER, InternalBukkitAccess.INSTANCE);
        pluginManager.registerEvents(PingCalculator.LISTENER, InternalBukkitAccess.INSTANCE);
        pluginManager.registerEvents(ProxyHelloPacketListener.INSTANCE, InternalBukkitAccess.INSTANCE);
        DefaultPermissions.registerPermission(
                "poseidon.anticheat.anti-xray.exempt",
                "Makes a player exempt from the server's anti-xray obfuscation",
                PermissionDefault.OP);
        DefaultPermissions.registerPermission(
                "poseidon.anticheat.quick-movement-flagging.bypass",
                "Allows a player to bypass the server's quick movement flagging",
                PermissionDefault.OP);
        DefaultPermissions.registerPermission(
                "poseidon.anticheat.wrong-movement-flagging.bypass",
                "Allows a player to bypass the server's wrong movement flagging",
                PermissionDefault.OP);
        DefaultPermissions.registerPermission(
                "poseidon.anticheat.flight-flagging.bypass",
                "Allows a player to bypass the server's flight flagging",
                PermissionDefault.OP);
    }

    public void postInitialize() {
        getWatchdogThread().start();
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

    public WatchdogThread getWatchdogThread() {
        return this.watchdogThread;
    }

    public ProtocolManagerImpl getProtocolManager() {
        return this.protocolManager;
    }
}
