package com.legacyminecraft.poseidon.config;

import com.legacyminecraft.poseidon.config.type.Duration;
import org.spongepowered.configurate.loader.HeaderMode;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.util.function.Consumer;

public final class PoseidonConfigurations {

    public static final String CONFIG_FOLDER = "config";
    public static final String GLOBAL_CONFIG_FILE_NAME = "global-config.yml";
    public static final String WORLD_DEFAULTS_FILE_NAME = "world-defaults.yml";
    public static final String WORLD_CONFIG_FILE_NAME = "world-config.yml";

    public static YamlConfigurationLoader.Builder createLoaderBuilder() {
        return YamlConfigurationLoader.builder()
                .indent(2)
                .nodeStyle(NodeStyle.BLOCK)
                .headerMode(HeaderMode.PRESET)
                .defaultOptions(opt -> opt.serializers(registerSerializers()));
    }

    private static Consumer<TypeSerializerCollection.Builder> registerSerializers() {
        return builder -> builder.register(Duration.SERIALIZER);
    }
}
