package com.legacyminecraft.poseidon.version;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PoseidonBuildInformation {

    private static final Logger log = LoggerFactory.getLogger(PoseidonBuildInformation.class);

    private final Properties versionProperties = new Properties();

    public void load() {
        try (InputStream in = PoseidonBuildInformation.class.getClassLoader().getResourceAsStream("version.properties")) {
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

    public String getShortGitCommit() {
        String commitSha = getGitCommit();
        return commitSha.length() <= 7 ? commitSha : commitSha.substring(0, 7);
    }

    private String getProperty(String key) {
        return this.versionProperties.getProperty(key, "unknown");
    }
}
