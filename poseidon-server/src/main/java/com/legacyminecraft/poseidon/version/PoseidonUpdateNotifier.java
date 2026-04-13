package com.legacyminecraft.poseidon.version;

import com.google.gson.JsonObject;
import com.legacyminecraft.poseidon.Poseidon;
import com.legacyminecraft.poseidon.service.ServiceClient;
import com.legacyminecraft.poseidon.service.ServiceClientException;
import com.legacyminecraft.poseidon.service.ServiceClientHttpException;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class PoseidonUpdateNotifier {

    private static final String GITHUB_API_URL = "https://api.github.com/repos/retromcorg/Project-Poseidon-V2/releases/latest";
    private static final String RELEASE_URL = "https://github.com/retromcorg/Project-Poseidon-V2/releases";
    private static final Logger log = LoggerFactory.getLogger(PoseidonUpdateNotifier.class);

    private static @Nullable PoseidonUpdateNotifier instance;

    private final ScheduledExecutorService executor;
    private final ServiceClient client;
    private final PoseidonBuildInformation buildInformation;
    private volatile String latestRelease;

    public PoseidonUpdateNotifier(ServiceClient client, PoseidonBuildInformation buildInformation) {
        this.executor = Executors.newSingleThreadScheduledExecutor(
                Thread.ofPlatform().name("UpdateNotifier").factory());
        this.client = client;
        this.buildInformation = buildInformation;
        this.latestRelease = this.buildInformation.getVersion();
    }

    public void start() {
        long interval = Poseidon.config().updateNotifier.interval.getNanos();
        this.executor.scheduleAtFixedRate(this::getLatestRelease, 0, interval, TimeUnit.NANOSECONDS);
    }

    public void getLatestRelease() {
        try {
            JsonObject object = this.client.get(URI.create(GITHUB_API_URL), JsonObject.class);
            this.latestRelease = object.get("tag_name").getAsString();

            if (isUpdateAvailable()) {
                log.info("A new release is available: {}", this.latestRelease);
                log.info("You are currently running release: {}", this.buildInformation.getVersion());
                log.info("Download the latest release here: {}", RELEASE_URL);
            } else if (Poseidon.config().updateNotifier.notifyIsRunningLatestRelease) {
                log.info("You are running the latest release: {}", this.buildInformation.getVersion());
            }
        } catch (ServiceClientException e) {
            if (e instanceof ServiceClientHttpException http) {
                log.warn("Failed to get latest release from GitHub API: returned response code {}", http.getResponse().statusCode());
            } else {
                log.warn("Failed to get latest release", e);
            }
        }
    }

    public boolean isUpdateAvailable() {
        return !this.latestRelease.equalsIgnoreCase(this.buildInformation.getVersion());
    }

    public void shutdown() {
        this.executor.shutdownNow();
        try {
            this.executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException _) {
        }
    }

    public static PoseidonUpdateNotifier getInstance() {
        if (instance == null) {
            instance = new PoseidonUpdateNotifier(Poseidon.getServiceClient(), Poseidon.getBuildInformation());
        }
        return instance;
    }
}
