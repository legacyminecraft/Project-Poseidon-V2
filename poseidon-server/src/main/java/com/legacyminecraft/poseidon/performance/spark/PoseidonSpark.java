package com.legacyminecraft.poseidon.performance.spark;

import com.legacyminecraft.poseidon.util.InternalBukkitAccess;
import me.lucko.spark.common.SparkPlatform;
import me.lucko.spark.common.SparkPlugin;
import me.lucko.spark.common.command.sender.CommandSender;
import me.lucko.spark.common.monitor.ping.PlayerPingProvider;
import me.lucko.spark.common.platform.PlatformInfo;
import me.lucko.spark.common.sampler.source.ClassSourceLookup;
import me.lucko.spark.common.sampler.source.SourceMetadata;
import me.lucko.spark.common.tick.TickHook;
import me.lucko.spark.common.tick.TickReporter;
import me.lucko.spark.common.util.SparkScheduledThreadPoolExecutor;
import org.bukkit.craftbukkit.CraftServer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.stream.Stream;

public final class PoseidonSpark implements SparkPlugin {

    private final CraftServer server;
    private final ScheduledExecutorService asyncScheduler;
    private final PoseidonTickHook tickHook;
    private final PoseidonTickReporter tickReporter;
    private final SparkPlatform platform;

    private boolean enabled;

    public PoseidonSpark(CraftServer server) {
        this.server = server;
        this.asyncScheduler = new SparkScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());
        this.tickHook = new PoseidonTickHook();
        this.tickReporter = new PoseidonTickReporter();
        this.platform = new SparkPlatform(this);
    }

    public void enable() {
        if (!this.enabled) {
            this.platform.enable();
            this.enabled = true;
        }
    }

    public void disable() {
        if (this.enabled) {
            this.platform.disable();
            this.enabled = false;
        }
    }

    public void executeCommand(org.bukkit.command.CommandSender sender, String[] args) {
        this.platform.executeCommand(new PoseidonCommandSender(sender), args);
    }

    public void onTickStart() {
        this.tickHook.onTick();
    }

    public void onTickEnd(double duration) {
        this.tickReporter.onTick(duration);
    }

    @Override
    public String getVersion() {
        return this.server.getVersion();
    }

    @Override
    public Path getPluginDirectory() {
        return Paths.get("spark");
    }

    @Override
    public String getCommandName() {
        return "spark";
    }

    @Override
    public Stream<? extends CommandSender> getCommandSenders() {
        return Stream.concat(
                Arrays.stream(this.server.getOnlinePlayers()),
                Stream.of(this.server.getServer().console)
        ).map(PoseidonCommandSender::new);
    }

    @Override
    public void executeAsync(Runnable task) {
        this.asyncScheduler.execute(task);
    }

    @Override
    public void executeSync(Runnable task) {
        this.server.getScheduler().scheduleSyncDelayedTask(InternalBukkitAccess.INSTANCE, task);
    }

    @Override
    public TickHook createTickHook() {
        return this.tickHook;
    }

    @Override
    public TickReporter createTickReporter() {
        return this.tickReporter;
    }

    @Override
    public ClassSourceLookup createClassSourceLookup() {
        return new PoseidonClassSourceLookup();
    }

    @Override
    public Collection<SourceMetadata> getKnownSources() {
        return SourceMetadata.gather(
                Arrays.asList(this.server.getPluginManager().getPlugins()),
                plugin -> plugin.getDescription().getName(),
                plugin -> plugin.getDescription().getVersion(),
                plugin -> String.join(", ", plugin.getDescription().getAuthors()),
                plugin -> plugin.getDescription().getDescription()
        );
    }

    @Override
    public PlayerPingProvider createPlayerPingProvider() {
        return new PoseidonPlayerPingProvider(this.server);
    }

    @Override
    public PlatformInfo getPlatformInfo() {
        return new PoseidonPlatformInfo(this.server);
    }

    @Override
    public void log(Level level, String msg) {
        this.server.getLogger().log(level, msg);
    }

    @Override
    public void log(Level level, String msg, Throwable throwable) {
        this.server.getLogger().log(level, msg, throwable);
    }
}
