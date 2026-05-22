package com.legacyminecraft.poseidon.network;

import com.legacyminecraft.poseidon.network.connection.PlayerConnection;
import com.legacyminecraft.poseidon.network.handler.PacketHandler;
import com.legacyminecraft.poseidon.network.handler.PacketHolder;
import com.legacyminecraft.poseidon.network.protocol.InboundPacket;
import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import net.minecraft.server.Packet106Transaction;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import java.util.Optional;

public final class PingCalculator {

    public final PacketHandler<OutboundPacket> OUTBOUND_HANDLER = new OutboundHandler();
    public final PacketHandler<InboundPacket> INBOUND_HANDLER = new InboundHandler();

    private final Int2LongOpenHashMap pings = new Int2LongOpenHashMap();

    public PingCalculator() {
        this.pings.defaultReturnValue(Long.MIN_VALUE);
    }

    private final class OutboundHandler implements PacketHandler<OutboundPacket> {
        @Override
        public void handlePacket(PlayerConnection connection, PacketHolder<OutboundPacket> holder) {
            if (!(holder.getPacket() instanceof Packet106Transaction transaction)) {
                return;
            }

            if (!transaction.c && transaction.b < 0) {
                PingCalculator.this.pings.put(transaction.b, System.nanoTime());
            }
        }
    }

    private final class InboundHandler implements PacketHandler<InboundPacket> {
        @Override
        public void handlePacket(PlayerConnection connection, PacketHolder<InboundPacket> holder) {
            if (!(holder.getPacket() instanceof Packet106Transaction transaction)) {
                return;
            }

            if (transaction.c && transaction.b < 0) {
                long now = System.nanoTime();
                long start = PingCalculator.this.pings.remove(transaction.b);
                if (start != Long.MIN_VALUE) {
                    holder.dropPacket();
                    int delta = (int) (now - start);
                    Optional.ofNullable((CraftPlayer) connection.getPlayer())
                            .map(CraftPlayer::getHandle)
                            .map(entityplayer -> entityplayer.netServerHandler)
                            .ifPresent(netServerHandler -> netServerHandler.ping.updateAndGet(ping -> (ping * 3 + delta) / 4));
                }
            }
        }
    }
}
