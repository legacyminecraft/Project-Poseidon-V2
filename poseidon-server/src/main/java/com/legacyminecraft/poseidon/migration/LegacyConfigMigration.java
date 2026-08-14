package com.legacyminecraft.poseidon.migration;

import com.legacyminecraft.poseidon.config.PoseidonConfigurations;
import com.legacyminecraft.poseidon.config.PoseidonGlobalConfig;
import com.legacyminecraft.poseidon.config.PoseidonWorldConfig;
import com.legacyminecraft.poseidon.config.type.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public final class LegacyConfigMigration {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss-SSS");
    private static final Logger log = LoggerFactory.getLogger(LegacyConfigMigration.class);

    public static void run() throws IOException {
        Path legacyConfigPath = Paths.get("poseidon.yml");
        if (Files.notExists(legacyConfigPath)) {
            return;
        }

        log.info("Migrating legacy configuration file");

        YamlConfigurationLoader loader = PoseidonConfigurations.createLoaderBuilder()
                .path(legacyConfigPath)
                .build();

        ConfigurationNode legacyConfig = loader.load();
        PoseidonGlobalConfig globalConfig = PoseidonGlobalConfig.getInstance();
        PoseidonWorldConfig worldDefaults = PoseidonWorldConfig.getDefaults();

        globalConfig.uuidSupport.storePlayerDataByUuid = legacyConfig.node("settings", "save-playerdata-by-uuid").getBoolean();
        globalConfig.logging.rollingLogFile.enabled = legacyConfig.node("settings", "per-day-log-file", "enabled").getBoolean();
        globalConfig.profiles.lookupBulkByNameUrl = legacyConfig.node("settings", "uuid-fetcher", "post", "value").getString();
        globalConfig.profiles.lookupByNameUrl = legacyConfig.node("settings", "uuid-fetcher", "get", "value").getString()
                .replace("{username}", "{name}");
        globalConfig.profiles.handleWrongNameCasing = legacyConfig.node("settings", "uuid-fetcher", "get", "enforce-case-sensitivity", "enabled").getBoolean()
                ? PoseidonGlobalConfig.Profiles.WrongNameCasingHandlingMode.REJECT
                : PoseidonGlobalConfig.Profiles.WrongNameCasingHandlingMode.KEEP;
        globalConfig.profiles.allowOfflineAccounts = legacyConfig.node("settings", "uuid-fetcher", "allow-graceful-uuids", "value").getBoolean();
        globalConfig.profiles.prefixOfflineUsernames = legacyConfig.node("settings", "cracked-username-prefix", "enabled").getBoolean();
        globalConfig.performance.watchdog.enabled = legacyConfig.node("settings", "watchdog", "enable").getBoolean();
        globalConfig.performance.watchdog.killServerAfter = Duration.of(legacyConfig.node("settings", "watchdog", "timeout", "value").getInt() + "s");
        globalConfig.performance.watchdog.threadDumps.enabled = legacyConfig.node("settings", "watchdog", "debug-timeout", "enabled").getBoolean();
        globalConfig.performance.watchdog.threadDumps.dumpThreadAfter = Duration.of(legacyConfig.node("settings", "watchdog", "debug-timeout", "value").getInt() + "s");
        globalConfig.network.packetRateLimiting.enabled = legacyConfig.node("settings", "packet-spam-detection", "enabled").getBoolean();
        globalConfig.network.proxySupport.proxyRequiredToConnect = legacyConfig.node("settings", "bungeecord", "bungee-mode", "enable").getBoolean();
        globalConfig.nameValidation.enabled = legacyConfig.node("settings", "check-username-validity", "enabled").getBoolean();
        globalConfig.nameValidation.allowedCharacters = Pattern.compile(legacyConfig.node("settings", "check-username-validity", "regex").getString());
        globalConfig.nameValidation.maximumLength = legacyConfig.node("settings", "check-username-validity", "max-length").getInt();
        globalConfig.nameValidation.minimumLength = legacyConfig.node("settings", "check-username-validity", "min-length").getInt();
        globalConfig.updateNotifier.enabled = legacyConfig.node("settings", "update-checker", "enabled").getBoolean();
        globalConfig.updateNotifier.notifyIsRunningLatestRelease = legacyConfig.node("settings", "update-checker", "notify-if-up-to-date", "enabled").getBoolean();
        globalConfig.updateNotifier.interval = Duration.of((legacyConfig.node("settings", "update-checker", "interval", "ticks").getLong() / 20) + "s");

        worldDefaults.spawnRandomizationRadius = legacyConfig.node("world-settings", "randomize-spawn").getBoolean() ? 10 : 0;
        worldDefaults.teleportToHighestSafeBlockOnJoin = legacyConfig.node("world-settings", "teleport-to-highest-safe-block").getBoolean();
        worldDefaults.anticheat.quickMovementFlagging.enabled = legacyConfig.node("world", "settings", "speed-hack-check", "enable").getBoolean();
        worldDefaults.anticheat.quickMovementFlagging.action = legacyConfig.node("world", "settings", "speed-hack-check", "teleport").getBoolean()
                ? PoseidonWorldConfig.Anticheat.MovementFlagAction.TELEPORT_BACK
                : PoseidonWorldConfig.Anticheat.MovementFlagAction.KICK;
        worldDefaults.anticheat.quickMovementFlagging.threshold = legacyConfig.node("world", "settings", "speed-hack-check", "distance").getInt() * 2;
        worldDefaults.entities.mobSpawnerEntityLimit.enabled = legacyConfig.node("world", "settings", "mob-spawner-area-limit", "enable").getBoolean();
        worldDefaults.entities.mobSpawnerEntityLimit.limit = legacyConfig.node("world", "settings", "mob-spawner-area-limit", "limit").getInt();
        worldDefaults.entities.mobSpawnerEntityLimit.radius = legacyConfig.node("world", "settings", "mob-spawner-area-limit", "chunk-radius").getInt() * 16;
        worldDefaults.chunks.regenerateCorruptChunks = legacyConfig.node("emergency", "debug", "regenerate-corrupt-chunks", "enable").getBoolean();
        worldDefaults.blocks.spongesTriggerBlockUpdates = !legacyConfig.node("fix", "optimize-sponges", "enabled").getBoolean();

        PoseidonGlobalConfig.save();
        PoseidonWorldConfig.saveDefaults();
        Files.move(legacyConfigPath, Paths.get("poseidon.yml." + FORMATTER.format(LocalDateTime.now()) + ".migrated"));

        log.info("Successfully migrated legacy configuration file");
    }
}
