package com.legacyminecraft.poseidon.network.login;

import net.minecraft.server.NetLoginHandler;

public final class SetProfileLoginStage implements LoginStage {

    @Override
    public void run(LoginProcessHandler loginProcessHandler) {
        NetLoginHandler.a(loginProcessHandler.getNetLoginHandler(), loginProcessHandler.getProfile());
    }
}
