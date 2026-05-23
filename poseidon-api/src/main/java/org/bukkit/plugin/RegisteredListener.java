package org.bukkit.plugin;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Stores relevant information for plugin listeners
 */
public class RegisteredListener {

    private final Listener listener;
    private final EventPriority priority; // Poseidon - Event.Priority -> EventPriority
    private final Plugin plugin;
    private final EventExecutor executor;
    private final boolean ignoreCancelled; // Poseidon

    // Poseidon - change signature
    public RegisteredListener(final Listener listener, final EventExecutor executor, final EventPriority priority, final Plugin plugin, final boolean ignoreCancelled) {
        this.listener = listener;
        this.priority = priority;
        this.plugin = plugin;
        this.executor = executor;
        this.ignoreCancelled = ignoreCancelled; // Poseidon
    }

    /**
     * Gets the listener for this registration
     * @return Registered Listener
     */
    public Listener getListener() {
        return listener;
    }

    /**
     * Gets the plugin for this registration
     * @return Registered Plugin
     */
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * Gets the priority for this registration
     * @return Registered Priority
     */
    public EventPriority getPriority() { // Poseidon - Event.Priority -> EventPriority
        return priority;
    }

    // Poseidon start
    /**
     * Whether this listener accepts cancelled events
     * @return True when ignoring cancelled events
     */
    public boolean isIgnoringCancelled() {
        return ignoreCancelled;
    }
    // Poseidon end

    /**
     * Calls the event executor
     * @param event The event
     */
    public void callEvent(Event event) throws EventException {
        // Poseidon start
        if (event instanceof Cancellable cancellable) {
            if (cancellable.isCancelled() && isIgnoringCancelled()) {
                return;
            }
        }
        // Poseidon end
        executor.execute(listener, event);
    }
}
