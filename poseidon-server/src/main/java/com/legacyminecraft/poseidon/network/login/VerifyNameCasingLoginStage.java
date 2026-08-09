package com.legacyminecraft.poseidon.network.login;

import com.legacyminecraft.poseidon.Poseidon;
import com.legacyminecraft.poseidon.profile.MinecraftProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VerifyNameCasingLoginStage implements LoginStage {

    private static final Logger log = LoggerFactory.getLogger(VerifyNameCasingLoginStage.class);

    @Override
    public void run(LoginProcessHandler loginProcessHandler) {
        if (!loginProcessHandler.getProfile().online()) {
            return;
        }

        String name = loginProcessHandler.getPlayerName();
        MinecraftProfile profile = loginProcessHandler.getProfile();
        if (!name.equals(profile.name())) {
            switch (Poseidon.getConfig().profiles.handleWrongNameCasing) {
                case KEEP -> {
                    MinecraftProfile newProfile = new MinecraftProfile(profile.id(), name, profile.online());
                    loginProcessHandler.setProfile(newProfile);
                }
                case REJECT -> {
                    log.info("Disconnecting {} as the correct name is '{}' and wrongly cased names should be rejected.", name, profile.name());
                    loginProcessHandler.disconnect("Wrongly cased name '" + name + "', correct name: '" + profile.name() + "'");
                }
            }
        }
    }
}
