package org.bukkit.plugin;

import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.config.Configuration;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.logging.Logger;

/**
 * Represents a Plugin
 */
public interface Plugin extends CommandExecutor {

    /**
     * Returns the folder that the plugin data's files are located in. The
     * folder may not yet exist.
     *
     * @return
     */
    File getDataFolder();

    /**
     * Returns the plugin.yaml file containing the details for this plugin
     *
     * @return Contents of the plugin.yaml file
     */
    PluginDescriptionFile getDescription();

    /**
     * Returns the main configuration file. It should be loaded.
     *
     * @return
     */
    Configuration getConfiguration();

    /**
     * Gets the associated PluginLoader responsible for this plugin
     *
     * @return PluginLoader that controls this plugin
     */
    PluginLoader getPluginLoader();

    /**
     * Returns the Server instance currently running this plugin
     *
     * @return Server running this plugin
     */
    Server getServer();

    // Poseidon start
    /**
     * Returns the plugin logger associated with this server's logger. The
     * returned logger automatically tags all log messages with the plugin's
     * name.
     *
     * @return Logger associated with this plugin
     */
    Logger getLogger();
    // Poseidon end

    /**
     * Returns a value indicating whether or not this plugin is currently enabled
     *
     * @return true if this plugin is enabled, otherwise false
     */
    boolean isEnabled();

    /**
     * Called when this plugin is disabled
     */
    void onDisable();

    /**
     * Called after a plugin is loaded but before it has been enabled.
     * When mulitple plugins are loaded, the onLoad() for all plugins is called before any onEnable() is called.
     */
    void onLoad();

    /**
     * Called when this plugin is enabled
     */
    void onEnable();

    /**
     * Simple boolean if we can still nag to the logs about things
     * @return boolean whether we can nag
     */
    boolean isNaggable();

    /**
     * Set naggable state
     * @param canNag is this plugin still naggable?
     */
    void setNaggable(boolean canNag);

    // Poseidon start - remove built-in database
//    /**
//     * Gets the {@link EbeanServer} tied to this plugin
//     *
//     * @return Ebean server instance
//     */
//    EbeanServer getDatabase();
    // Poseidon end

    /**
     * Gets a {@link ChunkGenerator} for use in a default world, as specified in the server configuration
     *
     * @param worldName Name of the world that this will be applied to
     * @param id Unique ID, if any, that was specified to indicate which generator was requested
     * @return ChunkGenerator for use in the default world generation
     */
    @Nullable ChunkGenerator getDefaultWorldGenerator(String worldName, @Nullable String id);
}
