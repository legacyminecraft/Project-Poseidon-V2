package com.legacyminecraft.poseidon.migration;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.legacyminecraft.poseidon.Poseidon;
import com.legacyminecraft.poseidon.config.PoseidonGlobalConfig;
import com.legacyminecraft.poseidon.profile.MinecraftProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public final class LegacyProfileCacheMigration {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss-SSS");
    private static final Logger log = LoggerFactory.getLogger(LegacyProfileCacheMigration.class);

    public static void run() {
        try {
            runThrowing();
        } catch (Exception e) {
            log.error("Failed to migrate legacy profile cache");
            throw new RuntimeException(e);
        }
    }

    private static void runThrowing() throws Exception {
        Path legacyCachePath = Paths.get("uuidcache.json");
        if (Files.notExists(legacyCachePath)) {
            return;
        }

        log.info("Migrating legacy profile cache");

        PoseidonGlobalConfig.getInstance().profiles.useLegacyUuidGeneration = true;
        PoseidonGlobalConfig.save();

        try (BufferedReader reader = Files.newBufferedReader(legacyCachePath)) {
            JsonArray legacyCache = (JsonArray) JsonParser.parseReader(reader);
            for (JsonElement element : legacyCache) {
                JsonObject entry = element.getAsJsonObject();
                UUID uuid = UUID.fromString(entry.get("uuid").getAsString());
                String name = entry.get("name").getAsString();
                boolean onlineUUID = entry.get("onlineUUID").getAsBoolean();
                ZonedDateTime expiresOn = Instant.ofEpochSecond(entry.get("expiresOn").getAsLong()).atZone(ZoneId.systemDefault());

                Poseidon.getProfileCache().addProfile(new MinecraftProfile(uuid, name, onlineUUID), expiresOn);
            }
        }

        Files.move(legacyCachePath, Paths.get("uuidcache.json." + FORMATTER.format(ZonedDateTime.now()) + ".migrated"));

        log.info("Successfully migrated legacy profile cache");
    }
}
