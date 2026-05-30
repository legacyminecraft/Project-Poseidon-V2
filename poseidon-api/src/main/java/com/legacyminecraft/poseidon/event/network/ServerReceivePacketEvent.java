package com.legacyminecraft.poseidon.event.network;

import com.legacyminecraft.poseidon.network.connection.PlayerConnection;
import com.legacyminecraft.poseidon.network.protocol.InboundPacket;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.Nullable;

/**
 * This event is fired when the server receives a packet from a player
 * connection. If the event is cancelled, the packet will not be handled by the
 * server.
 * <p>
 * Note that this event is fired asynchronously to avoid performance hits due
 * to synchronization.
 */
public class ServerReceivePacketEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final PlayerConnection connection;
    private final InboundPacket packet;
    private boolean cancelled = false;

    public ServerReceivePacketEvent(PlayerConnection connection, InboundPacket packet) {
        super(true);
        this.connection = connection;
        this.packet = packet;
    }

    /**
     * Returns the {@link PlayerConnection} which the packet was received from.
     *
     * @return the player connection
     */
    public PlayerConnection getConnection() {
        return this.connection;
    }

    /**
     * Returns the player which the packet was received from.
     * <p>
     * Note that this will return {@code null} if the player has not finished
     * the login process.
     *
     * @return the player, or null if the player has not finished logging in
     */
    public @Nullable Player getPlayer() {
        return this.connection.getPlayer();
    }

    /**
     * Returns the packet which was received from the player connection.
     *
     * @return the packet
     */
    public InboundPacket getPacket() {
        return this.packet;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
