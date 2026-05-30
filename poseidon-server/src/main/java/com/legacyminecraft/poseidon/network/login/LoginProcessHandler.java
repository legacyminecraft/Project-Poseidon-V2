package com.legacyminecraft.poseidon.network.login;

import com.legacyminecraft.poseidon.profile.MinecraftProfile;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetLoginHandler;

import java.util.Iterator;
import java.util.List;

public final class LoginProcessHandler implements Runnable {

    private static final List<LoginStage> LOGIN_STAGES = new ObjectArrayList<>();

    private final MinecraftServer server;
    private final NetLoginHandler netLoginHandler;
    private final String name;

    private MinecraftProfile profile;

    public LoginProcessHandler(MinecraftServer server, NetLoginHandler netLoginHandler, String name) {
        this.server = server;
        this.netLoginHandler = netLoginHandler;
        this.name = name;
    }

    @Override
    public void run() {
        Iterator<LoginStage> iterator = LOGIN_STAGES.iterator();
        while (MinecraftServer.isRunning(this.server) && this.netLoginHandler.isConnected() && iterator.hasNext()) {
            LoginStage loginStage = iterator.next();
            loginStage.run(this);
        }
    }

    public void disconnect(String message) {
        this.netLoginHandler.disconnect(message);
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public NetLoginHandler getNetLoginHandler() {
        return this.netLoginHandler;
    }

    public String getPlayerName() {
        return this.name;
    }

    public MinecraftProfile getProfile() {
        return this.profile;
    }

    public void setProfile(MinecraftProfile profile) {
        this.profile = profile;
    }

    static {
        // order is important, do not change!
        LOGIN_STAGES.add(new ValidateNameLoginStage());
        LOGIN_STAGES.add(new VerifySessionLoginStage());
        LOGIN_STAGES.add(new LookupProfileLoginStage());
        LOGIN_STAGES.add(new VerifyNameCasingLoginStage());
        LOGIN_STAGES.add(new CallPreLoginEventsLoginStage());
        LOGIN_STAGES.add(new SetProfileLoginStage());
    }
}
