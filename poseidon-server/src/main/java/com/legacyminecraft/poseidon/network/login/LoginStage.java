package com.legacyminecraft.poseidon.network.login;

@FunctionalInterface
public interface LoginStage {
    void run(LoginProcessHandler loginProcessHandler);
}
