package com.legacyminecraft.poseidon.network.handler;

import com.legacyminecraft.poseidon.Poseidon;
import com.legacyminecraft.poseidon.network.connection.PlayerConnection;
import com.legacyminecraft.poseidon.network.protocol.InboundPacket;
import it.unimi.dsi.fastutil.longs.LongArrayList;

public final class PacketRateLimitHandler implements PacketHandler<InboundPacket> {

    private final LongArrayList packetTimes = new LongArrayList();
    private boolean exceededRateLimit = false;

    @Override
    public void handlePacket(PlayerConnection connection, PacketHolder<InboundPacket> holder) {
        if (!Poseidon.getConfig().network.packetRateLimiting.enabled) {
            return;
        } else if (this.exceededRateLimit) {
            holder.dropPacket();
            return;
        }

        long now = System.nanoTime();
        if (!this.packetTimes.isEmpty()) {
            long delta = now - this.packetTimes.getLong(0);
            if (delta <= Poseidon.getConfig().network.packetRateLimiting.interval.getNanos()) {
                if (this.packetTimes.size() >= Poseidon.getConfig().network.packetRateLimiting.maxPacketRate) {
                    this.exceededRateLimit = true;
                    holder.dropPacket();
                    connection.disconnect("Exceeded packet rate limit");
                    return;
                }
            } else {
                this.packetTimes.clear();
            }
        }
        this.packetTimes.add(now);
    }
}
