package org.bukkit.event.server;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

/**
 * Called when a plugin is disabled.
 */
public class PluginDisableEvent extends PluginEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public PluginDisableEvent(Plugin plugin) {
        super(Type.PLUGIN_DISABLE, plugin);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
