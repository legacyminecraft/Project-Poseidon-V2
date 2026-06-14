package com.legacyminecraft.poseidon.config;

import com.legacyminecraft.poseidon.config.constraint.Positive;
import com.legacyminecraft.poseidon.config.constraint.PositiveOrZero;
import org.bukkit.Material;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.PostProcess;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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

    private static @Nullable PoseidonWorldConfig defaults;

    public static PoseidonWorldConfig getDefaults() {
        if (defaults == null) {
            loadDefaults();
        }
        return defaults;
    }

    public static synchronized void loadDefaults() {
        YamlConfigurationLoader loader = PoseidonConfigurations.createLoaderBuilder()
                .path(Paths.get(CONFIG_FOLDER).resolve(WORLD_DEFAULTS_FILE_NAME))
                .defaultOptions(opt -> opt.header(DEFAULTS_HEADER))
                .build();

        try {
            PoseidonWorldConfig worldDefaults = loader.load().get(PoseidonWorldConfig.class);
            loader.save(loader.createNode().set(worldDefaults));
            defaults = worldDefaults;
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized PoseidonWorldConfig load(Path worldFolder) {
        YamlConfigurationLoader loader = PoseidonConfigurations.createLoaderBuilder()
                .path(worldFolder.resolve(WORLD_CONFIG_FILE_NAME))
                .defaultOptions(opt -> opt.header(
                        WORLD_HEADER.formatted(CONFIG_FOLDER, WORLD_DEFAULTS_FILE_NAME, worldFolder.getFileName())))
                .build();

        try {
            ConfigurationNode worldDefaults = loader.createNode().set(getDefaults());
            ConfigurationNode worldConfig = loader.load();
            loader.save(worldConfig);

            worldConfig.mergeFrom(worldDefaults);
            return worldConfig.require(PoseidonWorldConfig.class);
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
    }

    public Anticheat anticheat;

    @ConfigSerializable
    public static final class Anticheat {
        public AntiXray antiXray;

        @ConfigSerializable
        public static final class AntiXray {
            public boolean enabled = false;
            @Positive
            public int minSegmentSize = 3;
            @Positive
            public int maxSegmentSize = 64;
            public List<Material> obfuscatedBlocks = List.of(
                    Material.STONE,
                    Material.DIRT,
                    Material.GRAVEL,
                    Material.GOLD_ORE,
                    Material.IRON_ORE,
                    Material.COAL_ORE,
                    Material.LAPIS_ORE,
                    Material.DIAMOND_ORE,
                    Material.REDSTONE_ORE,
                    Material.GLOWING_REDSTONE_ORE
            );
            public List<Material> replacementBlocks = List.of(
                    Material.GOLD_ORE,
                    Material.IRON_ORE,
                    Material.COAL_ORE,
                    Material.LAPIS_ORE,
                    Material.DIAMOND_ORE,
                    Material.REDSTONE_ORE,
                    Material.MOSSY_COBBLESTONE
            );

            @PostProcess
            private void postProcess() throws SerializationException {
                if (this.maxSegmentSize < this.minSegmentSize) {
                    throw new SerializationException("max-segment-size must not be smaller than min-segment-size");
                }

                for (Material material : this.obfuscatedBlocks) {
                    if (material.getId() <= 0 || material.getId() > 96) {
                        throw new SerializationException(material + " is not a valid material for obfuscated-blocks");
                    }
                }

                for (Material material : this.replacementBlocks) {
                    if (material.getId() <= 0 || material.getId() > 96) {
                        throw new SerializationException(material + " is not a valid material for replacement-blocks");
                    }
                }
            }
        }

        public QuickMovementFlagging quickMovementFlagging;

        @ConfigSerializable
        public static final class QuickMovementFlagging {
            public boolean enabled = true;
            @Positive
            public double threshold = 200.0;
            public MovementFlagAction action = MovementFlagAction.KICK;
        }

        public WrongMovementFlagging wrongMovementFlagging;

        @ConfigSerializable
        public static final class WrongMovementFlagging {
            public boolean enabled = true;
            @Positive
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
        public boolean spongesTriggerBlockUpdates = false;
        public boolean lockedChestsDecay = true;
    }

    public Chunks chunks;

    @ConfigSerializable
    public static final class Chunks {
        @PositiveOrZero
        public int chunkTickingRange = 9;
        public boolean regenerateCorruptChunks = false;
    }

    public Entities entities;

    @ConfigSerializable
    public static final class Entities {
        public MobCaps mobCaps;

        @ConfigSerializable
        public static final class MobCaps {
            @PositiveOrZero
            public int monsters = 70;
            @PositiveOrZero
            public int animals = 15;
            @PositiveOrZero
            public int waterMobs = 5;
        }

        public MobSpawnerEntityLimit mobSpawnerEntityLimit;

        @ConfigSerializable
        public static final class MobSpawnerEntityLimit {
            public boolean enabled = true;
            @PositiveOrZero
            public int limit = 150;
            @Positive
            public int radius = 128;
        }

        @PositiveOrZero
        public int mobSpawningRange = 8;
        public boolean perPlayerMobSpawning = false;
        public ItemEntityMerging itemEntityMerging;

        @ConfigSerializable
        public static final class ItemEntityMerging {
            public boolean enabled = false;
            @Positive
            public double horizontalRadius = 0.5;
            @Positive
            public double verticalRadius = 0.25;
        }

        public boolean fixPlayerDeathAnimation = true;
    }

    @PositiveOrZero
    public int spawnRandomizationRadius = 10;
    public boolean teleportToHighestSafeBlockOnJoin = false;
}
