package com.legacyminecraft.poseidon.config;

import org.jspecify.annotations.Nullable;
import org.slf4j.event.Level;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.loader.HeaderMode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Paths;

@ConfigSerializable
public final class PoseidonConfig {

    private static final String HEADER = """
        This is the configuration file for Poseidon.
        As you can see, there's a lot to configure. Some options may impact gameplay, so use
        with caution, and make sure you know what each option does before configuring.

        If you need help with the configuration or have any questions related to Poseidon,
        join us in our Discord or check the wiki page.

        File Reference: https://github.com/retromcorg/Project-Poseidon-V2/wiki/Configuration
        Wiki: https://github.com/retromcorg/Project-Poseidon-V2/wiki
        Discord: https://discord.gg/FwKg676""";

    private static @Nullable PoseidonConfig instance;

    public static PoseidonConfig getInstance() {
        if (instance == null) {
            load();
        }
        return instance;
    }

    public static synchronized void load() {
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(Paths.get("poseidon.yml"))
                .indent(2)
                .nodeStyle(NodeStyle.BLOCK)
                .defaultOptions(opt -> opt.header(HEADER))
                .headerMode(HeaderMode.PRESET)
                .build();

        try {
            PoseidonConfig config = loader.load().get(PoseidonConfig.class);
            loader.save(loader.createNode().set(config));
            instance = config;
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
    }

    public Logging logging;

    @ConfigSerializable
    public static final class Logging {
        public Level level = Level.INFO;
        public String consolePattern = "[%d{HH:mm:ss} %level]: %msg%n";
        public String filePattern = "[%d{yyyy-MM-dd HH:mm:ss}] [%thread/%level]: %msg%n";
        public String file = "server.log";
        public RollingLogFile rollingLogFile;

        @ConfigSerializable
        public static final class RollingLogFile {
            public boolean enabled = false;
            public String latestFile = "logs/latest.log";
            public String fileNamePattern = "logs/%d{yyyy-MM-dd}.log.gz";
        }
    }
}
