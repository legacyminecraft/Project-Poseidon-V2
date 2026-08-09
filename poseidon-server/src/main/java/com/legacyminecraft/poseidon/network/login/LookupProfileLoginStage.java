package com.legacyminecraft.poseidon.network.login;

import com.legacyminecraft.poseidon.Poseidon;
import com.legacyminecraft.poseidon.profile.MinecraftProfile;
import com.legacyminecraft.poseidon.profile.ProfileNotFoundException;
import com.legacyminecraft.poseidon.service.ServiceClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public final class LookupProfileLoginStage implements LoginStage {

    private static final Logger log = LoggerFactory.getLogger(LookupProfileLoginStage.class);

    @Override
    public void run(LoginProcessHandler loginProcessHandler) {
        String name = loginProcessHandler.getPlayerName();
        MinecraftProfile profile;
        Optional<MinecraftProfile> optional = Poseidon.getProfileCache().getProfile(name, true);
        if (optional.isPresent() && optional.get().online()) {
            profile = optional.get();
        } else {
            try {
                profile = Poseidon.getProfileService().lookupProfileByName(name);
            } catch (ProfileNotFoundException e) {
                if (Poseidon.getConfig().profiles.allowOfflineAccounts) {
                    if (Poseidon.getConfig().profiles.prefixOfflineUsernames && !name.startsWith(".")) {
                        name = "." + name;
                    }
                    profile = MinecraftProfile.createOffline(name);
                } else {
                    log.info("Disconnecting {} as they do not have an online profile and offline accounts are disallowed.", name);
                    loginProcessHandler.disconnect("Offline accounts are not supported");
                    return;
                }
            } catch (ServiceClientException e) {
                log.warn("Failed to lookup profile for {}", name, e);
                loginProcessHandler.disconnect("Failed to lookup profile");
                return;
            }
        }

        log.info("UUID of player {} is {}", name, profile.id());
        Poseidon.getProfileCache().addProfile(profile);
        loginProcessHandler.setProfile(profile);
    }
}
