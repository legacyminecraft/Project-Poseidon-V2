package com.legacyminecraft.poseidon.event.network;

import com.google.common.base.Preconditions;
import com.legacyminecraft.poseidon.network.ping.ServerIcon;
import com.legacyminecraft.poseidon.profile.PlayerProfile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * This event is fired when a server list ping was received. It allows full
 * modification of the ping response sent to the client.
 * <p>
 * If this event is cancelled, no response will be sent and the client
 * connection will be closed immediately.
 * <p>
 * Note that this event is fired asynchronously.
 */
public class ServerListPingEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final InetSocketAddress address;
    private final int clientProtocolVersion;
    private final InetSocketAddress virtualHost;

    private String version;
    private int protocolVersion;
    private int numPlayers;
    private int maxPlayers;
    private final List<PlayerProfile> playerSample = new ArrayList<>();
    private boolean hidePlayers;
    private String motd;
    private @Nullable ServerIcon serverIcon;

    private boolean cancelled;

    public ServerListPingEvent(
            InetSocketAddress address,
            int clientProtocolVersion,
            InetSocketAddress virtualHost,
            String version,
            int protocolVersion,
            int numPlayers,
            int maxPlayers,
            String motd,
            @Nullable ServerIcon serverIcon) {
        super(true);
        this.address = address;
        this.clientProtocolVersion = clientProtocolVersion;
        this.virtualHost = virtualHost;
        this.version = version;
        this.protocolVersion = protocolVersion;
        this.numPlayers = numPlayers;
        this.maxPlayers = maxPlayers;
        this.motd = motd;
        this.serverIcon = serverIcon;
    }

    /**
     * Returns the address of the client which sent the ping.
     *
     * @return the client address
     */
    public InetSocketAddress getAddress() {
        return this.address;
    }

    /**
     * Returns the protocol version of the client.
     *
     * @return the client protocol version
     */
    public int getClientProtocolVersion() {
        return this.clientProtocolVersion;
    }

    /**
     * Returns the virtual host the client connected to.
     *
     * @return the virtual host
     */
    public InetSocketAddress getVirtualHost() {
        return this.virtualHost;
    }

    /**
     * Returns the server version which will be sent to the client.
     *
     * @return the server version
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Sets the server version which will be sent to the client.
     *
     * @param version the server version
     */
    public void setVersion(String version) {
        Preconditions.checkArgument(version != null, "version cannot be null");
        this.version = version;
    }

    /**
     * Returns the server protocol version which will be sent to the client.
     *
     * @return the server protocol version
     */
    public int getProtocolVersion() {
        return this.protocolVersion;
    }

    /**
     * Sets the server protocol version which will be sent to the client.
     *
     * @param protocolVersion the server protocol version
     */
    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    /**
     * Returns the number of players on the server which will be sent to the
     * client.
     *
     * @return the number of players on the server
     */
    public int getNumPlayers() {
        return this.numPlayers;
    }

    /**
     * Sets the number of players on the server which will be sent to the
     * client.
     *
     * @param numPlayers the number of players on the server
     */
    public void setNumPlayers(int numPlayers) {
        Preconditions.checkArgument(numPlayers >= 0, "numPlayers must be greater than or equal zero");
        this.numPlayers = numPlayers;
    }

    /**
     * Returns the maximum allowed players on the server which will be sent to
     * the client.
     *
     * @return the maximum allowed players on the server
     */
    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    /**
     * Sets the maximum allowed players on the server which will be sent to
     * the client.
     *
     * @param maxPlayers the maximum allowed players on the server
     */
    public void setMaxPlayers(int maxPlayers) {
        Preconditions.checkArgument(maxPlayers >= 0, "maxPlayers must be greater than or equal zero");
        this.maxPlayers = maxPlayers;
    }

    /**
     * Returns a sample of online players on the server which will be sent to
     * the client.
     * <p>
     * The returned list is mutable.
     *
     * @return the mutable player sample
     */
    public List<PlayerProfile> getPlayerSample() {
        return this.playerSample;
    }

    /**
     * Returns whether all player-related information should be omitted in the
     * response. This includes {@link #getNumPlayers()}, {@link #getMaxPlayers()}
     * and {@link #getPlayerSample()}.
     *
     * @return {@code true} if player-related information should be hidden
     */
    public boolean shouldHidePlayers() {
        return this.hidePlayers;
    }

    /**
     * Sets whether all player-related information should be omitted in the
     * response. This includes {@link #getNumPlayers()}, {@link #getMaxPlayers()}
     * and {@link #getPlayerSample()}.
     *
     * @param hidePlayers if player-related information should be hidden
     */
    public void setHidePlayers(boolean hidePlayers) {
        this.hidePlayers = hidePlayers;
    }

    /**
     * Returns the server message of the day which will be sent to the client.
     *
     * @return the server message of the day
     */
    public String getMotd() {
        return this.motd;
    }

    /**
     * Sets the server message of the day which will be sent to the client.
     *
     * @param motd the server message of the day
     */
    public void setMotd(String motd) {
        Preconditions.checkNotNull(motd != null, "motd cannot be null");
        this.motd = motd;
    }

    /**
     * Returns the server icon which will be sent to the client.
     *
     * @return the server icon, or {@code null} if not specified
     */
    public @Nullable ServerIcon getServerIcon() {
        return this.serverIcon;
    }

    /**
     * Sets the server icon which will be sent to the client.
     *
     * @param serverIcon the server icon, or {@code null} to omit
     */
    public void setServerIcon(@Nullable ServerIcon serverIcon) {
        this.serverIcon = serverIcon;
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
