package com.legacyminecraft.poseidon.version;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PoseidonBuildInformation {

    private static final Logger log = LoggerFactory.getLogger(PoseidonBuildInformation.class);

    private static @Nullable PoseidonBuildInformation instance;

    private final Properties versionProperties = new Properties();

    public void load() {
        try (InputStream in = PoseidonBuildInformation.class.getResourceAsStream("version.properties")) {
            if (in != null) {
                this.versionProperties.load(in);
            }
        } catch (IOException e) {
            log.warn("Failed to load version.properties", e);
        }
    }

    public String getAppName() {
        return getProperty("app_name");
    }

    public String getVersion() {
        return getProperty("version");
    }

    public String getBuildType() {
        return getProperty("build_type");
    }

    public String getBuildTimestamp() {
        return getProperty("build_timestamp");
    }

    public String getGitCommit() {
        return getProperty("git_commit");
    }

    private String getProperty(String key) {
        return this.versionProperties.getProperty(key, "unknown");
    }

    public static PoseidonBuildInformation getInstance() {
        if (instance == null) {
            instance = new PoseidonBuildInformation();
        }
        return instance;
    }
}
