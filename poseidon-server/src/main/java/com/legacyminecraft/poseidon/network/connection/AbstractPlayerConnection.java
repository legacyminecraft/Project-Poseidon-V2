package com.legacyminecraft.poseidon.network.connection;

import com.google.common.base.Preconditions;
import com.google.common.net.InetAddresses;
import com.legacyminecraft.poseidon.event.messaging.PlayerRegisterChannelEvent;
import com.legacyminecraft.poseidon.event.messaging.PlayerUnregisterChannelEvent;
import com.legacyminecraft.poseidon.messaging.StandardMessenger;
import com.legacyminecraft.poseidon.network.login.LoginState;
import com.legacyminecraft.poseidon.network.ping.ServerListPingHandler;
import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import com.legacyminecraft.poseidon.network.proxy.ProxyConnectionDetails;
import net.minecraft.server.NetHandler;
import net.minecraft.server.NetServerHandler;
import net.minecraft.server.Packet250PluginMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public abstract class AbstractPlayerConnection implements PlayerConnection, INetworkManager {

    private final AtomicBoolean proxyConnection = new AtomicBoolean(false);
    private final AtomicBoolean supportsMessaging = new AtomicBoolean(false);
    private final Set<String> channels = ConcurrentHashMap.newKeySet();
    private final PacketRateLimiter packetRateLimiter = new PacketRateLimiter(this);
    private final PingCalculator pingCalculator = new PingCalculator();

    private volatile LoginState loginState = LoginState.INITIAL;
    private @Nullable ServerListPingHandler pingHandler;

    public abstract NetHandler getNetHandler();

    @Override
    public @Nullable Player getPlayer() {
        return getNetHandler() instanceof NetServerHandler netServerHandler ? netServerHandler.getPlayer() : null;
    }

    @Override
    public abstract void sendPacket(OutboundPacket packet);

    @Override
    public void sendPluginMessage(Plugin owningPlugin, String channel, byte[] message) {
        StandardMessenger.validatePluginMessage(Bukkit.getMessenger(), owningPlugin, channel, message);

        if (this.channels.contains(channel)) {
            sendPacket(new Packet250PluginMessage(channel, message));
        }
    }

    public void enablePluginMessaging() {
        this.supportsMessaging.compareAndSet(false, true);
    }

    @Override
    public Set<String> getListeningChannels() {
        return Set.copyOf(this.channels);
    }

    public void addChannel(String channel) {
        if (this.channels.add(channel)) {
            new PlayerRegisterChannelEvent(this, channel).callEvent();
        }
    }

    public void removeChannel(String channel) {
        if (this.channels.remove(channel)) {
            new PlayerUnregisterChannelEvent(this, channel).callEvent();
        }
    }

    public void notifyChannelsRegistered(Set<String> channels) {
        notifyChannels(channels, true);
    }

    public void notifyChannelsUnregistered(Set<String> channels) {
        notifyChannels(channels, false);
    }

    private void notifyChannels(Set<String> channels, boolean register) {
        String channel = register ? StandardMessenger.REGISTER_CHANNEL : StandardMessenger.UNREGISTER_CHANNEL;
        if (!channels.isEmpty() && this.supportsMessaging.get()) {
            byte[] encodedChannels = StandardMessenger.encodeChannels(channels);
            sendPacket(new Packet250PluginMessage(channel, encodedChannels));
        }
    }

    public void sendSupportedChannels() {
        if (this.supportsMessaging.get()) {
            Set<String> channels = Bukkit.getMessenger().getInboundChannels().stream()
                    .filter(channel -> !channel.equals(StandardMessenger.PROXY_HELLO_CHANNEL))
                    .collect(Collectors.toSet());

            if (!channels.isEmpty()) {
                byte[] encodedChannels = StandardMessenger.encodeChannels(channels);
                sendPacket(new Packet250PluginMessage(StandardMessenger.REGISTER_CHANNEL, encodedChannels));
            }
        }
    }

    @Override
    public void disconnect(String message) {
        Preconditions.checkArgument(message != null, "message cannot be null");
        getNetHandler().disconnect(message);
    }

    @Override
    public boolean isConnected() {
        return getNetHandler().isConnected();
    }

    @Override
    public boolean isProxyConnection() {
        return this.proxyConnection.get();
    }

    @Override
    public abstract InetSocketAddress getRawAddress();

    @Override
    public abstract InetSocketAddress getClientAddress();

    @Override
    public int getPing() {
        return this.pingCalculator.getPing();
    }

    public abstract void setClientAddress(InetSocketAddress address);

    public void onConnectionDetailsReceived(ProxyConnectionDetails details) {
        if (this.proxyConnection.compareAndSet(false, true)) {
            setClientAddress(new InetSocketAddress(InetAddresses.forString(details.sourceHost()), details.sourcePort()));
        }
    }

    public LoginState getLoginState() {
        return this.loginState;
    }

    public void setLoginState(LoginState newState) {
        Preconditions.checkArgument(newState != null, "newState cannot be null");
        this.loginState = newState;
    }

    public @Nullable ServerListPingHandler getPingHandler() {
        return this.pingHandler;
    }

    public ServerListPingHandler enablePingProtocol() {
        ServerListPingHandler pingHandler = new ServerListPingHandler(getClientAddress());
        this.pingHandler = pingHandler;
        return pingHandler;
    }

    public PacketRateLimiter getPacketRateLimiter() {
        return this.packetRateLimiter;
    }

    public PingCalculator getPingCalculator() {
        return this.pingCalculator;
    }
}
