package org.bukkit;

import com.legacyminecraft.poseidon.messaging.Messenger;
import com.legacyminecraft.poseidon.network.ping.ServerIcon;
import com.legacyminecraft.poseidon.network.protocol.ProtocolManager;
import com.legacyminecraft.poseidon.persistence.PersistentDataContainer;
import com.legacyminecraft.poseidon.profile.PlayerProfile;
import org.bukkit.World.Environment;
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
 * Represents the Bukkit core, for version and Server singleton handling
 */
public final class Bukkit {
    private static Server server;

    /**
     * Static class cannot be initialized.
     */
    private Bukkit() {}

    /**
     * Gets the current {@link Server} singleton
     *
     * @return Server instance being ran
     */
    public static Server getServer() {
        return server;
    }

    /**
     * Attempts to set the {@link Server} singleton.
     *
     * This cannot be done if the Server is already set.
     *
     * @param server Server instance
     */
    public static void setServer(Server server) {
        if (Bukkit.server != null) {
            throw new UnsupportedOperationException("Cannot redefine singleton Server");
        }

        Bukkit.server = server;
        // Poseidon start - add build information
        server.getLogger().info(getVersionString());
    }

    public static String getVersionString() {
        return server.getVersionString();
    }
    // Poseidon end

    public static String getName() {
        return server.getName();
    }

    public static String getVersion() {
        return server.getVersion();
    }

    public static Player[] getOnlinePlayers() {
        return server.getOnlinePlayers();
    }

    public static int getMaxPlayers() {
        return server.getMaxPlayers();
    }

    public static int getPort() {
        return server.getPort();
    }

    public static int getViewDistance() {
        return server.getViewDistance();
    }

    public static String getIp() {
        return server.getIp();
    }

    public static String getServerName() {
        return server.getServerName();
    }

    public static String getServerId() {
        return server.getServerId();
    }

    // Poseidon start - implement server list ping protocol
    public static String getMotd() {
        return server.getMotd();
    }

    public static void setMotd(String motd) {
        server.setMotd(motd);
    }

    public static @Nullable ServerIcon getServerIcon() {
        return server.getServerIcon();
    }

    public static void setServerIcon(@Nullable ServerIcon serverIcon) {
        server.setServerIcon(serverIcon);
    }
    // Poseidon end

    public static boolean getAllowNether() {
        return server.getAllowNether();
    }

    public static boolean hasWhitelist() {
        return server.hasWhitelist();
    }

    public static int broadcastMessage(String message) {
        return server.broadcastMessage(message);
    }

    public static String getUpdateFolder() {
        return server.getUpdateFolder();
    }

    public static @Nullable Player getPlayer(String name) {
        return server.getPlayer(name);
    }

    public static @Nullable Player getPlayerExact(String name) {
        return server.getPlayerExact(name);
    }

    // Poseidon start - profile API
    public static @Nullable Player getPlayer(UUID id) {
        return server.getPlayer(id);
    }
    // Poseidon end

    public static List<Player> matchPlayer(String name) {
        return server.matchPlayer(name);
    }

    public static PluginManager getPluginManager() {
        return server.getPluginManager();
    }

    public static BukkitScheduler getScheduler() {
        return server.getScheduler();
    }

    // Poseidon start - implement plugin messaging
    public static Messenger getMessenger() {
        return server.getMessenger();
    }
    // Poseidon end

    public static ServicesManager getServicesManager() {
        return server.getServicesManager();
    }

    // Poseidon start - network API
    public static ProtocolManager getProtocolManager() {
        return server.getProtocolManager();
    }
    // Poseidon end

    public static List<World> getWorlds() {
        return server.getWorlds();
    }

    public static @Nullable World createWorld(String name, Environment environment) {
        return server.createWorld(name, environment);
    }

    public static @Nullable World createWorld(String name, Environment environment, long seed) {
        return server.createWorld(name, environment, seed);
    }

    public static @Nullable World createWorld(String name, Environment environment, ChunkGenerator generator) {
        return server.createWorld(name, environment, generator);
    }

    public static @Nullable World createWorld(String name, Environment environment, long seed, ChunkGenerator generator) {
        return server.createWorld(name, environment, seed, generator);
    }

    public static boolean unloadWorld(String name, boolean save) {
        return server.unloadWorld(name, save);
    }

    public static boolean unloadWorld(World world, boolean save) {
        return server.unloadWorld(world, save);
    }

    public static @Nullable World getWorld(String name) {
        return server.getWorld(name);
    }

    public static @Nullable World getWorld(UUID uid) {
        return server.getWorld(uid);
    }

    public static @Nullable MapView getMap(short id) {
        return server.getMap(id);
    }

    public static MapView createMap(World world) {
        return server.createMap(world);
    }

    public static void reload() {
        server.reload();
    }

    public static Logger getLogger() {
        return server.getLogger();
    }

    public static @Nullable PluginCommand getPluginCommand(String name) {
        return server.getPluginCommand(name);
    }

    public static void savePlayers() {
        server.savePlayers();
    }

    public static boolean dispatchCommand(CommandSender sender, String commandLine) {
        return server.dispatchCommand(sender, commandLine);
    }

    // Poseidon start - remove built-in database
    /*public static void configureDbConfig(ServerConfig config) {
        server.configureDbConfig(config);
    }*/
    // Poseidon end

    public static boolean addRecipe(Recipe recipe) {
        return server.addRecipe(recipe);
    }

    public static Map<String, String[]> getCommandAliases() {
        return server.getCommandAliases();
    }

    public static int getSpawnRadius() {
        return server.getSpawnRadius();
    }

    public static void setSpawnRadius(int value) {
        server.setSpawnRadius(value);
    }

    public static boolean getOnlineMode() {
        return server.getOnlineMode();
    }

    public static boolean getAllowFlight() {
        return server.getAllowFlight();
    }

    public static void shutdown() {
        server.shutdown();
    }

    public static int broadcast(String message, String permission) {
        return server.broadcast(message, permission);
    }

    public static OfflinePlayer getOfflinePlayer(String name) {
        return server.getOfflinePlayer(name);
    }

    // Poseidon start - profile API
    public static @Nullable OfflinePlayer getOfflinePlayerIfCached(String name) {
        return server.getOfflinePlayerIfCached(name);
    }

    public static OfflinePlayer getOfflinePlayer(UUID id) {
        return server.getOfflinePlayer(id);
    }

    public static PlayerProfile createProfile(UUID id, String name, boolean onlineMode) {
        return server.createProfile(id, name, onlineMode);
    }

    public static PlayerProfile createOfflineProfile(String name) {
        return server.createOfflineProfile(name);
    }
    // Poseidon end

    public static Set<String> getIPBans() {
        return server.getIPBans();
    }

    public static void banIP(String address) {
        server.banIP(address);
    }

    public static void unbanIP(String address) {
        server.unbanIP(address);
    }
    
    public static Set<OfflinePlayer> getBannedPlayers() {
        return server.getBannedPlayers();
    }

    public static void setWhitelist(boolean value) {
        server.setWhitelist(value);
    }

    public static Set<OfflinePlayer> getWhitelistedPlayers() {
        return server.getWhitelistedPlayers();
    }

    public static void reloadWhitelist() {
        server.reloadWhitelist();
    }

    // Poseidon start
    public static boolean isPrimaryThread() {
        return server.isPrimaryThread();
    }
    // Poseidon end

    // Poseidon start - PersistentDataContainer API
    public static PersistentDataContainer createPersistentDataContainer() {
        return server.createPersistentDataContainer();
    }
    // Poseidon end
}
