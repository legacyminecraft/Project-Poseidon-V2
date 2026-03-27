package org.bukkit.event.player;

import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

/**
 * Called when a player leaves a server
 */
public class PlayerQuitEvent extends PlayerEvent {

    private @Nullable String quitMessage;

    public PlayerQuitEvent(Player who, String quitMessage) {
        super(Type.PLAYER_QUIT, who);
        this.quitMessage = quitMessage;
    }

    /**
     * Gets the quit message to send to all online players
     *
     * @return string quit message
     */
    public @Nullable String getQuitMessage() {
        return quitMessage;
    }

    /**
     * Sets the quit message to send to all online players
     *
     * @param quitMessage quit message
     */
    public void setQuitMessage(@Nullable String quitMessage) {
        this.quitMessage = quitMessage;
    }
}
