package com.legacyminecraft.poseidon.event.messaging;

import com.legacyminecraft.poseidon.network.connection.PlayerConnection;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event is fired when a {@link PlayerConnection} registers or unregisters
 * for a plugin channel.
 */
public abstract class PlayerChannelEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final PlayerConnection connection;
    private final String channel;

    public PlayerChannelEvent(PlayerConnection connection, String channel) {
        this.connection = connection;
        this.channel = channel;
    }

    /**
     * Returns the player connection involved in this event.
     *
     * @return the player connection
     */
    public final PlayerConnection getConnection() {
        return this.connection;
    }

    /**
     * Returns the plugin channel involved in this event.
     *
     * @return the plugin channel
     */
    public final String getChannel() {
        return this.channel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
