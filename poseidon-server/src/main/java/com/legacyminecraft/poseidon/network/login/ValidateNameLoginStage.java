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
            String name = loginProcessHandler.getPlayerName();
            if (name.length() < minLength) {
                loginProcessHandler.disconnect("Name too short, minimum " + minLength + " characters allowed");
            } else if (name.length() > maxLength) {
                loginProcessHandler.disconnect("Name too long, maximum " + maxLength + " characters allowed");
            } else if (!allowedChars.matcher(name).matches()) {
                loginProcessHandler.disconnect("Name has invalid characters, allowed characters: " + allowedChars);
            }
        }
    }
}
