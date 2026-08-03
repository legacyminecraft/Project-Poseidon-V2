package org.bukkit;

import com.avaje.ebean.config.ServerConfig;
import com.legacyminecraft.poseidon.messaging.Messenger;
import com.legacyminecraft.poseidon.messaging.PluginMessageRecipient;
import com.legacyminecraft.poseidon.network.ping.ServerIcon;
import com.legacyminecraft.poseidon.network.protocol.ProtocolManager;
import com.legacyminecraft.poseidon.persistence.PersistentDataContainer;
import com.legacyminecraft.poseidon.profile.PlayerProfile;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.Recipe;
import org.bukkit.map.MapView;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Represents a server implementation
 */
public interface Server extends PluginMessageRecipient { // Poseidon - extends PluginMessageRecipient
    /**
     * Used for all administrative messages, such as an operator using a command.
     *
     * For use in {@link #broadcast(java.lang.String, java.lang.String)}
     */
    String BROADCAST_CHANNEL_ADMINISTRATIVE = "bukkit.broadcast.admin";

    /**
     * Used for all announcement messages, such as informing users that a player has joined.
     *
     * For use in {@link #broadcast(java.lang.String, java.lang.String)}
     */
    String BROADCAST_CHANNEL_USERS = "bukkit.broadcast.user";

    // Poseidon start - add build information
    /**
     * Gets the message describing which version of the server is running.
     *
     * @return message describing which version of the server is running
     */
    String getVersionString();
    // Poseidon end

    /**
     * Gets the name of this server implementation
     *
     * @return name of this server implementation
     */
    String getName();

    /**
     * Gets the version string of this server implementation.
     *
     * @return version of this server implementation
     */
    String getVersion();

    /**
     * Gets a list of all currently logged in players
     *
     * @return An array of Players that are currently online
     */
    Player[] getOnlinePlayers();

    /**
     * Get the maximum amount of players which can login to this server
     *
     * @return The amount of players this server allows
     */
    int getMaxPlayers();

    /**
     * Get the game port that the server runs on
     *
     * @return The port number of this server
     */
    int getPort();

    /**
     * Get the view distance from this server.
     *
     * @return The view distance from this server.
     */
    int getViewDistance();

    /**
     * Get the IP that this server is bound to or empty string if not specified
     *
     * @return The IP string that this server is bound to, otherwise empty string
     */
    String getIp();

    /**
     * Get the name of this server
     *
     * @return The name of this server
     */
    String getServerName();

    /**
     * Get an ID of this server. The ID is a simple generally alphanumeric
     * ID that can be used for uniquely identifying this server.
     *
     * @return The ID of this server
     */
    String getServerId();

    // Poseidon start - implement server list ping protocol
    /**
     * Returns the message that is displayed on the server list.
     *
     * @return the message of the day
     */
    String getMotd();

    /**
     * Sets the message that is displayed on the server list.
     *
     * @param motd the message of the day
     */
    void setMotd(String motd);

    /**
     * Returns the server icon that is displayed on the server list.
     *
     * @return the server icon, or {@code null} if not set
     */
    @Nullable ServerIcon getServerIcon();

    /**
     * Sets the server icon that is displayed on the server list.
     *
     * @param serverIcon the server icon, or {@code null} to remove
     */
    void setServerIcon(@Nullable ServerIcon serverIcon);
    // Poseidon end

    /**
     * Gets whether this server allows the Nether or not.
     *
     * @return Whether this server allows the Nether or not.
     */
    boolean getAllowNether();

    /**
     * Gets whether this server has a whitelist or not.
     *
     * @return Whether this server has a whitelist or not.
     */
    boolean hasWhitelist();

    /**
     * Sets the whitelist on or off
     *
     * @param value true if whitelist is on, otherwise false
     */
    void setWhitelist(boolean value);

    /**
     * Gets a list of whitelisted players
     *
     * @return Set containing all whitelisted players
     */
    Set<OfflinePlayer> getWhitelistedPlayers();

    /**
     * Reloads the whitelist from disk
     */
    void reloadWhitelist();

    /**
     * Broadcast a message to all players.
     *
     * This is the same as calling {@link #broadcast(java.lang.String, java.lang.String)} to {@link #BROADCAST_CHANNEL_USERS}
     *
     * @param message the message
     * @return the number of players
     */
    int broadcastMessage(String message);

    /**
     * Gets the name of the update folder. The update folder is used to safely update
     * plugins at the right moment on a plugin load.
     *
     * @return The name of the update folder
     */
    String getUpdateFolder();

    /**
     * Gets a player object by the given username
     *
     * This method may not return objects for offline players
     *
     * @param name Name to look up
     * @return Player if it was found, otherwise null
     */
    @Nullable Player getPlayer(String name);

    /**
     * Gets the player with the exact given name, case insensitive
     *
     * @param name Exact name of the player to retrieve
     * @return Player object or null if not found
     */
    @Nullable Player getPlayerExact(String name);

    // Poseidon start - profile API
    /**
     * Gets the player with the given UUID.
     *
     * @param id UUID of the player to retrieve
     * @return a player object if one was found, null otherwise
     */
    @Nullable Player getPlayer(UUID id);
    // Poseidon end

    /**
     * Attempts to match any players with the given name, and returns a list
     * of all possibly matches
     *
     * This list is not sorted in any particular order. If an exact match is found,
     * the returned list will only contain a single result.
     *
     * @param name Name to match
     * @return List of all possible players
     */
    List<Player> matchPlayer(String name);

    /**
     * Gets the PluginManager for interfacing with plugins
     *
     * @return PluginManager for this Server instance
     */
    PluginManager getPluginManager();

    /**
     * Gets the Scheduler for managing scheduled events
     *
     * @return Scheduler for this Server instance
     */
    BukkitScheduler getScheduler();

    // Poseidon start - implement plugin messaging
    /**
     * Gets this server's {@link Messenger} for managing registrations of
     * plugin channels.
     *
     * @return this server's messenger
     */
    Messenger getMessenger();
    // Poseidon end

    /**
     * Gets a services manager
     *
     * @return Services manager
     */
    ServicesManager getServicesManager();

    // Poseidon start - network API
    /**
     * Gets the protocol manager responsible for registering and
     * unregistering packets.
     *
     * @return the protocol manager
     */
    ProtocolManager getProtocolManager();
    // Poseidon end

    /**
     * Gets a list of all worlds on this server
     *
     * @return A list of worlds
     */
    List<World> getWorlds();

    /**
     * Creates or loads a world with the given name.
     * If the world is already loaded, it will just return the equivalent of
     * getWorld(name)
     *
     * @param name Name of the world to load
     * @param environment Environment type of the world
     * @return Newly created or loaded World
     */
    @Nullable World createWorld(String name, World.Environment environment);

    /**
     * Creates or loads a world with the given name.
     * If the world is already loaded, it will just return the equivalent of
     * getWorld(name)
     *
     * @param name Name of the world to load
     * @param environment Environment type of the world
     * @param seed Seed value to create the world with
     * @return Newly created or loaded World
     */
    @Nullable World createWorld(String name, World.Environment environment, long seed);

    /**
     * Creates or loads a world with the given name.
     * If the world is already loaded, it will just return the equivalent of
     * getWorld(name)
     *
     * @param name Name of the world to load
     * @param environment Environment type of the world
     * @param generator ChunkGenerator to use in the construction of the new world
     * @return Newly created or loaded World
     */
    @Nullable World createWorld(String name, World.Environment environment, ChunkGenerator generator);

    /**
     * Creates or loads a world with the given name.
     * If the world is already loaded, it will just return the equivalent of
     * getWorld(name)
     *
     * @param name Name of the world to load
     * @param environment Environment type of the world
     * @param seed Seed value to create the world with
     * @param generator ChunkGenerator to use in the construction of the new world
     * @return Newly created or loaded World
     */
    @Nullable World createWorld(String name, World.Environment environment, long seed, ChunkGenerator generator);

     /**
     * Unloads a world with the given name.
     *
     * @param name Name of the world to unload
     * @param save Whether to save the chunks before unloading.
     * @return Whether the action was Successful
     */
     boolean unloadWorld(String name, boolean save);

    /**
     * Unloads the given world.
     *
     * @param world The world to unload
     * @param save Whether to save the chunks before unloading.
     * @return Whether the action was Successful
     */
    boolean unloadWorld(@Nullable World world, boolean save);

    /**
     * Gets the world with the given name
     *
     * @param name Name of the world to retrieve
     * @return World with the given name, or null if none exists
     */
    @Nullable World getWorld(String name);

    /**
     * Gets the world from the given Unique ID
     *
     * @param uid Unique ID of the world to retrieve.
     * @return World with the given Unique ID, or null if none exists.
     */
    @Nullable World getWorld(UUID uid);
    
    /**
     * Gets the map from the given item ID.
     * 
     * @param id ID of the map to get.
     * @return The MapView if it exists, or null otherwise.
     */
    @Nullable MapView getMap(short id);
    
    /**
     * Create a new map with an automatically assigned ID.
     * 
     * @param world The world the map will belong to.
     * @return The MapView just created.
     */
    MapView createMap(World world);

    /**
     * Reloads the server, refreshing settings and plugin information
     */
    void reload();

    /**
     * Returns the primary logger associated with this server instance
     *
     * @return Logger associated with this server
     */
    Logger getLogger();

    /**
     * Gets a {@link PluginCommand} with the given name or alias
     *
     * @param name Name of the command to retrieve
     * @return PluginCommand if found, otherwise null
     */
    @Nullable PluginCommand getPluginCommand(String name);

    /**
     * Writes loaded players to disk
     */
    void savePlayers();

    /**
     * Dispatches a command on the server, and executes it if found.
     *
     * @param cmdLine command + arguments. Example: "test abc 123"
     * @return targetFound returns false if no target is found.
     * @throws CommandException Thrown when the executor for the given command fails with an unhandled exception
     */
    boolean dispatchCommand(CommandSender sender, String commandLine);

    /**
     * Populates a given {@link ServerConfig} with values attributes to this server
     *
     * @param config ServerConfig to populate
     */
    void configureDbConfig(ServerConfig config);

    /**
     * Adds a recipe to the crafting manager.
     * @param recipe The recipe to add.
     * @return True to indicate that the recipe was added.
     */
    boolean addRecipe(Recipe recipe);

    /**
     * Gets a list of command aliases defined in the server properties.
     *
     * @return Map of aliases to command names
     */
    Map<String, String[]> getCommandAliases();

    /**
     * Gets the radius, in blocks, around each worlds spawn point to protect
     *
     * @return Spawn radius, or 0 if none
     */
    int getSpawnRadius();

    /**
     * Sets the radius, in blocks, around each worlds spawn point to protect
     *
     * @param value New spawn radius, or 0 if none
     */
    void setSpawnRadius(int value);

    /**
     * Gets whether the Server is in online mode or not.
     *
     * @return Whether the server is in online mode.
     */
    boolean getOnlineMode();

    /**
     * Gets whether this server allows flying or not.
     *
     * @return Whether this server allows flying or not.
     */
    boolean getAllowFlight();

    /**
     * Shutdowns the server, stopping everything.
     */
    void shutdown();

    /**
     * Broadcasts the specified message to every user with the given permission
     *
     * @param message Message to broadcast
     * @param permission Permission the users must have to receive the broadcast
     * @return Amount of users who received the message
     */
    int broadcast(String message, String permission);

    /**
     * Gets the player by the given name, regardless if they are offline or
     * online.
     * <p>
     * This method may involve a blocking web request to get the UUID for the
     * given name.
     * <p>
     * This will return an object even if the player does not exist. To this
     * method, all players will exist.
     *
     * @param name the name the player to retrieve
     * @return an offline player
     */
    OfflinePlayer getOfflinePlayer(String name);

    // Poseidon start - profile API
    /**
     * Gets the player by the given name, regardless if they are offline or
     * online.
     * <p>
     * This will not make a web request to get the UUID for the given name,
     * thus this method will not block. However this method will return
     * {@code null} if the player is not cached.
     *
     * @param name the name of the player to retrieve
     * @return an offline player if cached, {@code null} otherwise
     */
    @Nullable OfflinePlayer getOfflinePlayerIfCached(String name);

    /**
     * Gets the player by the given UUID, regardless if they are offline or
     * online.
     * <p>
     * This will return an object even if the player does not exist. To this
     * method, all players will exist.
     *
     * @param id the UUID of the player to retrieve
     * @return an offline player
     */
    OfflinePlayer getOfflinePlayer(UUID id);

    /**
     * Creates a new {@link PlayerProfile} from the specified UUID, name and
     * online mode.
     *
     * @param id the UUID
     * @param name the name
     * @param onlineMode if the profile is an online profile
     * @return a PlayerProfile object
     */
    PlayerProfile createProfile(UUID id, String name, boolean onlineMode);

    /**
     * Creates a new offline {@link PlayerProfile} from the specified name.
     * <p>
     * This will generate a UUID for the profile based on the name. The name
     * is case-insensitive, meaning that the same UUID will be generated for
     * names which only differ in casing.
     *
     * @param name the name
     * @return a PlayerProfile object
     */
    PlayerProfile createOfflineProfile(String name);
    // Poseidon end

    /**
     * Gets a set containing all current IPs that are banned
     *
     * @return Set containing banned IP addresses
     */
    Set<String> getIPBans();

    /**
     * Bans the specified address from the server
     *
     * @param address IP address to ban
     */
    void banIP(String address);

    /**
     * Unbans the specified address from the server
     *
     * @param address IP address to unban
     */
    void unbanIP(String address);

    /**
     * Gets a set containing all banned players
     *
     * @return Set containing banned players
     */
    Set<OfflinePlayer> getBannedPlayers();

    // Poseidon start
    /**
     * Checks the current thread against the expected primary thread for the
     * server.
     *
     * @return true if the current thread matches the expected primary thread,
     *     false otherwise
     */
    boolean isPrimaryThread();
    // Poseidon end

    // Poseidon start - PersistentDataContainer API
    /**
     * Creates a new persistent data container.
     *
     * @return the created persistent data container
     */
    PersistentDataContainer createPersistentDataContainer();
    // Poseidon end
}
