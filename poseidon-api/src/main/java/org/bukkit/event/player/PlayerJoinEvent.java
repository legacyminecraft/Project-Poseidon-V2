package org.bukkit.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.Nullable;

/**
 * Called when a player joins a server
 */
public class PlayerJoinEvent extends PlayerEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private @Nullable String joinMessage;

    public PlayerJoinEvent(Player playerJoined, String joinMessage) {
        super(Type.PLAYER_JOIN, playerJoined);
        this.joinMessage = joinMessage;
    }

    /**
     * Gets the join message to send to all online players
     *
     * @return string join message
     */
    public @Nullable String getJoinMessage() {
        return joinMessage;
    }

    /**
     * Sets the join message to send to all online players
     *
     * @param joinMessage join message
     */
    public void setJoinMessage(@Nullable String joinMessage) {
        this.joinMessage = joinMessage;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
