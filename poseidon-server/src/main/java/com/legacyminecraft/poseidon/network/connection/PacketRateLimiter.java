package com.legacyminecraft.poseidon.network.connection;

import com.legacyminecraft.poseidon.Poseidon;
import com.legacyminecraft.poseidon.event.network.ServerReceivePacketEvent;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public final class PacketRateLimiter {

    public static final Listener LISTENER = new Listener();

    private final PlayerConnection connection;
    private final LongArrayList packetTimes = new LongArrayList();

    private volatile boolean exceededRateLimit = false;

    public PacketRateLimiter(PlayerConnection connection) {
        this.connection = connection;
    }

    public void onReceivePacket(ServerReceivePacketEvent event) {
        if (this.exceededRateLimit) {
            event.setCancelled(true);
            return;
        }

        long now = System.nanoTime();
        if (!this.packetTimes.isEmpty()) {
            long delta = now - this.packetTimes.getLong(0);
            if (delta <= Poseidon.getConfig().network.packetRateLimiting.interval.getNanos()) {
                if (this.packetTimes.size() >= Poseidon.getConfig().network.packetRateLimiting.maxPacketRate) {
                    this.exceededRateLimit = true;
                    event.setCancelled(true);
                    this.connection.disconnect("Exceeded packet rate limit");
                    return;
                }
            } else {
                this.packetTimes.clear();
            }
        }
        this.packetTimes.add(now);
    }

    public static final class Listener implements org.bukkit.event.Listener {
        @EventHandler(priority = EventPriority.LOWEST)
        public void onServerReceivePacket(ServerReceivePacketEvent event) {
            if (!Poseidon.getConfig().network.packetRateLimiting.enabled) {
                return;
            }
            if (event.getConnection() instanceof AbstractPlayerConnection connection) {
                connection.getPacketRateLimiter().onReceivePacket(event);
            }
        }
    }
}
