package com.legacyminecraft.poseidon.network.connection;

import com.legacyminecraft.poseidon.event.network.ServerReceivePacketEvent;
import com.legacyminecraft.poseidon.event.network.ServerSendPacketEvent;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import net.minecraft.server.Packet106Transaction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.concurrent.atomic.AtomicInteger;

public final class PingCalculator {

    public static final Listener LISTENER = new Listener();

    private final Int2LongOpenHashMap pings = new Int2LongOpenHashMap();
    private final AtomicInteger ping = new AtomicInteger(0);

    public PingCalculator() {
        this.pings.defaultReturnValue(Long.MIN_VALUE);
    }

    public int getPing() {
        return this.ping.get() / 1_000_000;
    }

    public void onSendPacket(ServerSendPacketEvent event) {
        if (!(event.getPacket() instanceof Packet106Transaction transaction)) {
            return;
        }

        if (!transaction.c && transaction.b < 0) {
            this.pings.put(transaction.b, System.nanoTime());
        }
    }

    public void onReceivePacket(ServerReceivePacketEvent event) {
        if (!(event.getPacket() instanceof Packet106Transaction transaction)) {
            return;
        }

        if (transaction.c && transaction.b < 0) {
            long now = System.nanoTime();
            long start = this.pings.remove(transaction.b);
            if (start != Long.MIN_VALUE) {
                event.setCancelled(true);
                int delta = (int) (now - start);
                this.ping.updateAndGet(ping -> (ping * 3 + delta) / 4);
            }
        }
    }

    public static final class Listener implements org.bukkit.event.Listener {
        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onServerSendPacket(ServerSendPacketEvent event) {
            if (event.getConnection() instanceof AbstractPlayerConnection connection) {
                connection.getPingCalculator().onSendPacket(event);
            }
        }

        @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
        public void onServerReceivePacket(ServerReceivePacketEvent event) {
            if (event.getConnection() instanceof AbstractPlayerConnection connection) {
                connection.getPingCalculator().onReceivePacket(event);
            }
        }
    }
}
