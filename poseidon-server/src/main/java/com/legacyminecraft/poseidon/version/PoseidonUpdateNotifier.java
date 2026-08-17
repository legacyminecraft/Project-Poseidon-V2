package com.legacyminecraft.poseidon.version;

import com.legacyminecraft.poseidon.Poseidon;
import com.legacyminecraft.poseidon.service.ServiceClient;
import com.legacyminecraft.poseidon.service.ServiceClientException;
import com.legacyminecraft.poseidon.service.ServiceClientHttpException;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class PoseidonUpdateNotifier {

    private static final Logger log = LoggerFactory.getLogger(PoseidonUpdateNotifier.class);

    private final ScheduledExecutorService executor;
    private final ServiceClient client;
    private final PoseidonBuildInformation buildInformation;

    private volatile @Nullable GitHubRelease latestRelease;

    public PoseidonUpdateNotifier(ServiceClient client, PoseidonBuildInformation buildInformation) {
        this.executor = Executors.newSingleThreadScheduledExecutor(
                Thread.ofPlatform().name("Update Notifier").factory());
        this.client = client;
        this.buildInformation = buildInformation;
    }

    public void start() {
        if (Poseidon.getConfig().updateNotifier.enabled) {
            long interval = Poseidon.getConfig().updateNotifier.interval.getNanos();
            this.executor.scheduleAtFixedRate(this::fetchLatestRelease, 0, interval, TimeUnit.NANOSECONDS);
        } else {
            log.info("The update notifier is disabled. The server will not check for new releases.");
        }
    }

    public void shutdown() {
        this.executor.shutdownNow();
        try {
            this.executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.warn("Failed to shut down update notifier", e);
        }
    }

    public void fetchLatestRelease() {
        try {
            String repository = Poseidon.getConfig().updateNotifier.githubRepository;
            String url = "https://api.github.com/repos/" + repository + "/releases/latest";
            this.latestRelease = this.client.get(url, GitHubRelease.class);

            if (isUpdateAvailable()) {
                log.info("A new release of {} is available: {}", repository, this.latestRelease.tag());
                log.info("You are currently running release: {}", getCurrentBuild());
                log.info("Download the latest release here: {}", this.latestRelease.url());
            }
        } catch (ServiceClientException e) {
            if (e instanceof ServiceClientHttpException http) {
                log.warn("Failed to get latest release from GitHub API: returned response code {}", http.getResponse().statusCode());
            } else {
                log.warn("Failed to get latest release", e);
            }
        }
    }

    public @Nullable GitHubRelease getLatestRelease() {
        return this.latestRelease;
    }

    public boolean isUpdateAvailable() {
        GitHubRelease latestRelease = this.latestRelease;
        return latestRelease != null && !latestRelease.tag().equalsIgnoreCase(getCurrentBuild());
    }

    private String getCurrentBuild() {
        return this.buildInformation.implVersion();
    }
}
