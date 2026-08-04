package com.legacyminecraft.poseidon.config.transformation.global;

import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;

public final class GlobalConfigTransformations {

    public static final int LATEST_VERSION = 1;

    private static final ConfigurationTransformation.Versioned TRANSFORMATIONS
            = ConfigurationTransformation.versionedBuilder()
            .addVersion(LATEST_VERSION, ConfigurationTransformation.empty())
            .build();

    public static void apply(ConfigurationNode node) throws ConfigurateException {
        TRANSFORMATIONS.apply(node);
    }
}
