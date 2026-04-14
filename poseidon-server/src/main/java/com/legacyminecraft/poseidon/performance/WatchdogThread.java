package com.legacyminecraft.poseidon.performance;

import com.legacyminecraft.poseidon.Poseidon;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

public final class WatchdogThread extends Thread {

    private static final Logger log = LoggerFactory.getLogger(WatchdogThread.class);

    private final Thread serverThread;
    private final AtomicBoolean tickOccurred;

    public WatchdogThread(Thread serverThread) {
        this.serverThread = serverThread;
        this.tickOccurred = new AtomicBoolean(true);

        setName("Watchdog Thread");
        setDaemon(true);
        setPriority(Thread.MIN_PRIORITY);
    }

    public void tick() {
        this.tickOccurred.set(true);
    }

    @Override
    public void run() {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        long lastTick = System.nanoTime();
        boolean dumpedThread = false;

        while (MinecraftServer.isRunning(server)) {
            long now = System.nanoTime();
            if (Poseidon.getConfig().performance.watchdog.enabled) {
                if (this.tickOccurred.compareAndSet(true, false)) {
                    lastTick = now;
                    dumpedThread = false;
                } else {
                    long timeout = now - lastTick;
                    long killTimeout = Poseidon.getConfig().performance.watchdog.killServerAfter.getNanos();

                    if (timeout >= killTimeout) {
                        log.error("The server has stopped responding, killing the process due to the kill timeout being exceeded.");
                        dumpThread(Level.ERROR);
                        LockSupport.parkNanos(100_000_000L);
                        System.exit(1);
                    } else {
                        log.warn("The server has not responded for {} seconds.", timeout / 1_000_000_000);
                        boolean threadDumpsEnabled = Poseidon.getConfig().performance.watchdog.threadDumps.enabled;
                        long dumpTimeout = Poseidon.getConfig().performance.watchdog.threadDumps.dumpThreadAfter.getNanos();

                        if (threadDumpsEnabled && !dumpedThread && timeout >= dumpTimeout) {
                            dumpThread(Level.WARN);
                            dumpedThread = true;
                        }
                    }
                }
            }

            LockSupport.parkNanos(3_000_000_000L);
        }
    }

    private void dumpThread(Level level) {
        log.makeLoggingEventBuilder(level).log("------------------------------------------------------------");
        log.makeLoggingEventBuilder(level).log("Server thread dump:");
        for (StackTraceElement element : this.serverThread.getStackTrace()) {
            log.makeLoggingEventBuilder(level).log(element.toString());
        }
        log.makeLoggingEventBuilder(level).log("------------------------------------------------------------");
    }
}
