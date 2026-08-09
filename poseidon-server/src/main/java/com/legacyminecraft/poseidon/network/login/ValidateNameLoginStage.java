package com.legacyminecraft.poseidon.network.login;

import com.legacyminecraft.poseidon.Poseidon;

import java.util.regex.Pattern;

public final class ValidateNameLoginStage implements LoginStage {

    @Override
    public void run(LoginProcessHandler loginProcessHandler) {
        if (Poseidon.getConfig().nameValidation.enabled) {
            int minLength = Poseidon.getConfig().nameValidation.minimumLength;
            int maxLength = Poseidon.getConfig().nameValidation.maximumLength;
            Pattern allowedChars = Poseidon.getConfig().nameValidation.allowedCharacters;
            String name = loginProcessHandler.getProfile().name();

            if (name.length() < minLength) {
                loginProcessHandler.disconnect("Your name is too short, minimum length: " + minLength);
                return;
            }

            boolean prefixed = Poseidon.getConfig().profiles.prefixOfflineUsernames && name.startsWith(".");
            if (name.length() > maxLength) {
                loginProcessHandler.disconnect("Your name is too long, maximum length: " + (prefixed ? (maxLength - 1) : maxLength));
                return;
            }

            if (prefixed) {
                name = name.substring(1);
            }

            if (!allowedChars.matcher(name).matches()) {
                loginProcessHandler.disconnect("Your name is invalid, allowed characters: " + allowedChars);
            }
        }
    }
}
