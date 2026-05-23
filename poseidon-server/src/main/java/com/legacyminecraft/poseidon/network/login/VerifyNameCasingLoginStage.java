package com.legacyminecraft.poseidon.network.login;

import com.legacyminecraft.poseidon.Poseidon;
import com.legacyminecraft.poseidon.profile.MinecraftProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VerifyNameCasingLoginStage implements LoginStage {

    private static final Logger log = LoggerFactory.getLogger(VerifyNameCasingLoginStage.class);

    @Override
    public void run(LoginProcessHandler loginProcessHandler) {
        if (!loginProcessHandler.getProfile().onlineMode()) {
            return;
        }

        String name = loginProcessHandler.getPlayerName();
        MinecraftProfile profile = loginProcessHandler.getProfile();
        if (!name.equals(profile.name())) {
            switch (Poseidon.getConfig().profiles.handleLoginsWithWrongNameCasing) {
                case KEEP -> {
                    MinecraftProfile newProfile = new MinecraftProfile(profile.id(), name, profile.onlineMode());
                    loginProcessHandler.setProfile(newProfile);
                }
                case REJECT -> {
                    log.info("Disconnecting {} as the correct name is '{}' and wrongly cased names should be rejected.", name, profile.name());
                    loginProcessHandler.disconnect("Invalid name '" + name + "', correct name is '" + profile.name() + "'");
                }
            }
        }
    }
}
