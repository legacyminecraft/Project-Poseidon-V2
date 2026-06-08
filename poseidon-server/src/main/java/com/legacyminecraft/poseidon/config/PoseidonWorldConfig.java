package com.legacyminecraft.poseidon.config;

import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.legacyminecraft.poseidon.config.PoseidonConfigurations.CONFIG_FOLDER;
import static com.legacyminecraft.poseidon.config.PoseidonConfigurations.WORLD_CONFIG_FILE_NAME;
import static com.legacyminecraft.poseidon.config.PoseidonConfigurations.WORLD_DEFAULTS_FILE_NAME;

@ConfigSerializable
public final class PoseidonWorldConfig {

    private static final String DEFAULTS_HEADER = """
        This is the world defaults configuration file for Poseidon.
        As you can see, there's a lot to configure. Some options may impact gameplay, so use
        with caution, and make sure you know what each option does before configuring.

        If you need help with the configuration or have any questions related to Poseidon,
        join us in our Discord or check the wiki page.

        File Reference: https://github.com/legacyminecraft/Project-Poseidon-V2/wiki/World_Configuration
        Wiki: https://github.com/legacyminecraft/Project-Poseidon-V2/wiki
        Discord: https://discord.gg/FwKg676""";

    private static final String WORLD_HEADER = """
        This is a world configuration file for Poseidon.
        This file may start empty but can be filled with settings to override ones in %s/%s
        
        World: %s""";

    public static synchronized void loadDefaults() {
        YamlConfigurationLoader loader = PoseidonConfigurations.createLoaderBuilder()
                .path(Paths.get(CONFIG_FOLDER).resolve(WORLD_DEFAULTS_FILE_NAME))
                .defaultOptions(opt -> opt.header(DEFAULTS_HEADER))
                .build();

        try {
            PoseidonWorldConfig worldDefaults = loader.load().get(PoseidonWorldConfig.class);
            loader.save(loader.createNode().set(worldDefaults));
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized PoseidonWorldConfig load(Path worldFolder) {
        YamlConfigurationLoader worldDefaultsLoader = PoseidonConfigurations.createLoaderBuilder()
                .path(Paths.get(CONFIG_FOLDER).resolve(WORLD_DEFAULTS_FILE_NAME))
                .defaultOptions(opt -> opt.header(DEFAULTS_HEADER))
                .build();

        YamlConfigurationLoader worldConfigLoader = PoseidonConfigurations.createLoaderBuilder()
                .path(worldFolder.resolve(WORLD_CONFIG_FILE_NAME))
                .defaultOptions(opt -> opt.header(
                        WORLD_HEADER.formatted(CONFIG_FOLDER, WORLD_DEFAULTS_FILE_NAME, worldFolder.getFileName())))
                .build();

        try {
            ConfigurationNode worldDefaults = worldDefaultsLoader.load();
            worldDefaults.require(PoseidonWorldConfig.class);

            ConfigurationNode worldConfig = worldConfigLoader.load();
            worldConfigLoader.save(worldConfig);

            worldConfig.mergeFrom(worldDefaults);
            return worldConfig.require(PoseidonWorldConfig.class);
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
    }

    public Anticheat anticheat;

    @ConfigSerializable
    public static final class Anticheat {
        public QuickMovementFlagging quickMovementFlagging;

        @ConfigSerializable
        public static final class QuickMovementFlagging {
            public boolean enabled = true;
            public double threshold = 200.0;
            public MovementFlagAction action = MovementFlagAction.KICK;
        }

        public WrongMovementFlagging wrongMovementFlagging;

        @ConfigSerializable
        public static final class WrongMovementFlagging {
            public boolean enabled = true;
            public double threshold = 0.0625;
            public MovementFlagAction action = MovementFlagAction.TELEPORT_BACK;
        }

        public enum MovementFlagAction {
            KICK,
            TELEPORT_BACK
        }
    }

    public Blocks blocks;

    @ConfigSerializable
    public static final class Blocks {
        public boolean fixPistonPhysics = true;
    }

    public Entities entities;

    @ConfigSerializable
    public static final class Entities {
        public MobCaps mobCaps;

        @ConfigSerializable
        public static final class MobCaps {
            public int monsters = 70;
            public int animals = 15;
            public int waterMobs = 5;
        }

        public MobSpawnerEntityLimit mobSpawnerEntityLimit;

        @ConfigSerializable
        public static final class MobSpawnerEntityLimit {
            public boolean enabled = true;
            public int limit = 150;
            public int radius = 128;
        }

        public int mobSpawningRange = 8;
        public boolean perPlayerMobSpawning = false;
        public ItemEntityMerging itemEntityMerging;

        @ConfigSerializable
        public static final class ItemEntityMerging {
            public boolean enabled = false;
            public double horizontalRadius = 0.5;
            public double verticalRadius = 0.25;
        }
    }

    public int spawnRandomizationRadius = 10;
    public boolean teleportToHighestSafeBlockOnJoin = false;
}
