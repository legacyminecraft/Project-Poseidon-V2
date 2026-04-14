package com.legacyminecraft.poseidon.config;

import com.legacyminecraft.poseidon.config.type.Duration;
import org.jspecify.annotations.Nullable;
import org.slf4j.event.Level;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.loader.HeaderMode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Paths;
import java.util.regex.Pattern;

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
                .defaultOptions(opt -> opt
                        .header(HEADER)
                        .serializers(builder -> builder.register(Duration.SERIALIZER)))
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

    public UpdateNotifier updateNotifier;
    public Logging logging;
    public Performance performance;
    public Services services;
    public Profiles profiles;
    public UuidSupport uuidSupport;
    public NameValidation nameValidation;

    @ConfigSerializable
    public static final class UpdateNotifier {
        public boolean enabled = true;
        public Duration interval = Duration.of("6h");
        public boolean notifyIsRunningLatestRelease = true;
    }

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

    @ConfigSerializable
    public static final class Performance {
        public Watchdog watchdog;
        public TickLoop tickLoop;

        @ConfigSerializable
        public static final class Watchdog {
            public boolean enabled = true;
            public Duration killServerAfter = Duration.of("120s");
            public ThreadDumps threadDumps;

            @ConfigSerializable
            public static final class ThreadDumps {
                public boolean enabled = true;
                public Duration dumpThreadAfter = Duration.of("10s");
            }
        }

        @ConfigSerializable
        public static final class TickLoop {
            public Duration sprintUntilTimeBehind = Duration.of("2s");
        }
    }

    @ConfigSerializable
    public static final class Services {
        public String profileHost = "https://api.minecraftservices.com";
        public String sessionHost = "https://sessionserver.mojang.com";
    }

    @ConfigSerializable
    public static final class Profiles {
        public boolean allowOfflineProfiles = true;
        public Duration invalidateCachedProfilesAfter = Duration.of("30d");
        public WrongNameCasingHandlingMode handleLoginsWithWrongNameCasing = WrongNameCasingHandlingMode.KEEP;

        public enum WrongNameCasingHandlingMode {
            KEEP,
            CORRECT,
            REJECT
        }
    }

    @ConfigSerializable
    public static final class UuidSupport {
        public boolean storePlayerDataByUuid = true;
    }

    @ConfigSerializable
    public static final class NameValidation {
        public boolean enabled = true;
        public int minimumLength = 3;
        public int maximumLength = 16;
        public Pattern allowedCharacters = Pattern.compile("[A-Za-z0-9_]*");
    }
}
