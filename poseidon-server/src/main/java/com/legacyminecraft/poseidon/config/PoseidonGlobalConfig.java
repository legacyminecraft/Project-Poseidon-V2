package com.legacyminecraft.poseidon.config;

import com.legacyminecraft.poseidon.config.constraint.Max;
import com.legacyminecraft.poseidon.config.constraint.Min;
import com.legacyminecraft.poseidon.config.constraint.Positive;
import com.legacyminecraft.poseidon.config.transformation.global.GlobalConfigTransformations;
import com.legacyminecraft.poseidon.config.type.Duration;
import org.bukkit.ChatColor;
import org.jspecify.annotations.Nullable;
import org.slf4j.event.Level;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.PostProcess;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.legacyminecraft.poseidon.config.PoseidonConfigurations.CONFIG_FOLDER;
import static com.legacyminecraft.poseidon.config.PoseidonConfigurations.GLOBAL_CONFIG_FILE_NAME;

@ConfigSerializable
public final class PoseidonGlobalConfig {

    private static final String HEADER = """
        This is the global configuration file for Poseidon.
        As you can see, there's a lot to configure. Some options may impact gameplay, so use
        with caution, and make sure you know what each option does before configuring.

        If you need help with the configuration or have any questions related to Poseidon,
        join us in our Discord or check the wiki page.

        File Reference: https://github.com/legacyminecraft/Project-Poseidon-V2/wiki/Global_Configuration
        Wiki: https://github.com/legacyminecraft/Project-Poseidon-V2/wiki
        Discord: https://discord.gg/FwKg676""";

    public static boolean isFirstLoad = false;

    private static @Nullable PoseidonGlobalConfig instance;

    public static PoseidonGlobalConfig getInstance() {
        if (instance == null) {
            load();
        }
        return instance;
    }

    public static synchronized void load() {
        Path path = Paths.get(CONFIG_FOLDER).resolve(GLOBAL_CONFIG_FILE_NAME);
        isFirstLoad = Files.notExists(path);

        YamlConfigurationLoader loader = PoseidonConfigurations.createLoaderBuilder()
                .path(path)
                .defaultOptions(opt -> opt.header(HEADER))
                .build();

        try {
            ConfigurationNode node = loader.load();
            if (!isFirstLoad) {
                GlobalConfigTransformations.apply(node);
            }
            PoseidonGlobalConfig globalConfig = node.require(PoseidonGlobalConfig.class);
            loader.save(loader.createNode().set(globalConfig));
            instance = globalConfig;
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
    }

    public int version = GlobalConfigTransformations.LATEST_VERSION;

    public UpdateNotifier updateNotifier;

    @ConfigSerializable
    public static final class UpdateNotifier {
        public boolean enabled = true;
        public Duration interval = Duration.of("6h");
        public boolean notifyIsRunningLatestRelease = true;
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

        public List<String> redactedCommands = List.of();

        @PostProcess
        private void postProcess() {
            if (isFirstLoad) {
                this.redactedCommands = List.of(
                        "^authme changepassword .*",
                        "^authme register .*",
                        "^changepass .*",
                        "^changepassword .*",
                        "^changepw .*",
                        "^cpw .*",
                        "^l .*",
                        "^login .*",
                        "^reg .*",
                        "^register .*",
                        "^unregister .*",
                        "^xauth changepass .*",
                        "^xauth changepassword .*",
                        "^xauth changepw .*",
                        "^xauth cpw .*",
                        "^xauth register .*"
                );
            }
        }
    }

    public Performance performance;

    @ConfigSerializable
    public static final class Performance {
        public Watchdog watchdog;

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

        public TickLoop tickLoop;

        @ConfigSerializable
        public static final class TickLoop {
            public Duration sprintUntilTimeBehind = Duration.of("2s");
        }
    }

    public Network network;

    @ConfigSerializable
    public static final class Network {
        public Duration timeout = Duration.of("30000ms");
        public ConnectionThrottling connectionThrottling;

        @ConfigSerializable
        public static final class ConnectionThrottling {
            public boolean enabled = true;
            public Duration interval = Duration.of("5000ms");
            @Positive
            public int threshold = 1;
            public List<InetAddress> excludedAddresses;
        }

        public NettyIo nettyIo;

        @ConfigSerializable
        public static final class NettyIo {
            public boolean enabled = false;
            public boolean useNativeTransport = true;
            @Positive
            public int threads = 4;
        }

        public ProxySupport proxySupport;

        @ConfigSerializable
        public static final class ProxySupport {
            private static final String RANDOM_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
            public boolean enabled = false;
            public boolean proxyRequiredToConnect = true;
            public String secret = "changeme";

            @PostProcess
            private void postProcess() {
                if (isFirstLoad) {
                    this.secret = new SecureRandom().ints(0, RANDOM_CHARS.length())
                            .limit(20)
                            .mapToObj(RANDOM_CHARS::charAt)
                            .map(String::valueOf)
                            .collect(Collectors.joining());
                }
            }
        }

        public PingProtocol pingProtocol;

        @ConfigSerializable
        public static final class PingProtocol {
            public boolean enabled = false;
            public String motd = "A Minecraft Server";
            public boolean sendPlayerSample = true;
            @Positive
            public int maxSampleSize = 10;

            @PostProcess
            private void postProcess() {
                this.motd = ChatColor.translateAlternateColorCodes('&', this.motd);
            }
        }

        public PacketRateLimiting packetRateLimiting;

        @ConfigSerializable
        public static final class PacketRateLimiting {
            public boolean enabled = true;
            @Positive
            public int maxPacketRate = 500;
            public Duration interval = Duration.of("7s");
        }

        @Min(0)
        @Max(9)
        public int chunkPacketCompressionLevel = 6;
        @Positive
        public int maxChunkPacketsPerTick = 3;
    }

    public Sessions sessions;

    @ConfigSerializable
    public static final class Sessions {
        public String verifySessionUrl = "https://sessionserver.mojang.com/session/minecraft/hasJoined";
    }

    public Profiles profiles;

    @ConfigSerializable
    public static final class Profiles {
        public String lookupByNameUrl = "https://api.minecraftservices.com/minecraft/profile/lookup/name/{name}";
        public String lookupByIdUrl = "https://api.minecraftservices.com/minecraft/profile/lookup/{uuid}";
        public String lookupBulkByNameUrl = "https://api.minecraftservices.com/minecraft/profile/lookup/bulk/byname";
        public boolean allowOfflineAccounts = true;
        public boolean prefixOfflineUsernames = false;
        public Duration invalidateCachedProfilesAfter = Duration.of("30d");
        public WrongNameCasingHandlingMode handleWrongNameCasing = WrongNameCasingHandlingMode.KEEP;

        public enum WrongNameCasingHandlingMode {
            KEEP,
            CORRECT,
            REJECT
        }
    }

    public UuidSupport uuidSupport;

    @ConfigSerializable
    public static final class UuidSupport {
        public boolean storePlayerDataByUuid = true;
    }

    public NameValidation nameValidation;

    @ConfigSerializable
    public static final class NameValidation {
        public boolean enabled = true;
        @Min(1)
        @Max(16)
        public int minimumLength = 3;
        @Min(1)
        @Max(16)
        public int maximumLength = 16;
        public Pattern allowedCharacters = Pattern.compile("[A-Za-z0-9_]*");
    }

    public Commands commands;

    @ConfigSerializable
    public static final class Commands {
        public List<String> hiddenPlugins = List.of();
    }
}
