package com.legacyminecraft.poseidon.config.transformation.world;

import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;

public final class WorldConfigTransformations {

    public static final int LATEST_VERSION = 1;

    private static final ConfigurationTransformation.Versioned DEFAULTS_TRANSFORMATIONS
            = ConfigurationTransformation.versionedBuilder()
            .addVersion(LATEST_VERSION, ConfigurationTransformation.empty())
            .build();

    private static final ConfigurationTransformation.Versioned WORLD_TRANSFORMATIONS
            = ConfigurationTransformation.versionedBuilder()
            .addVersion(LATEST_VERSION, ConfigurationTransformation.empty())
            .build();

    public static void applyToDefaults(ConfigurationNode node) throws ConfigurateException {
        DEFAULTS_TRANSFORMATIONS.apply(node);
    }

    public static void applyToWorld(ConfigurationNode node) throws ConfigurateException {
        WORLD_TRANSFORMATIONS.apply(node);
    }
}
