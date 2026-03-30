package org.bukkit.event;

/**
 * A transitional class to avoid breaking plugins using custom events.
 */
@Deprecated
public class TransitionalCustomEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
