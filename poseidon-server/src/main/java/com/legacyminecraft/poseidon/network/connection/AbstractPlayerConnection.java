package com.legacyminecraft.poseidon.network.connection;

import com.google.common.base.Preconditions;
import com.legacyminecraft.poseidon.event.messaging.PlayerRegisterChannelEvent;
import com.legacyminecraft.poseidon.event.messaging.PlayerUnregisterChannelEvent;
import com.legacyminecraft.poseidon.messaging.StandardMessenger;
import com.legacyminecraft.poseidon.network.login.LoginState;
import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import com.legacyminecraft.poseidon.network.proxy.ProxyConnectionDetails;
import com.legacyminecraft.poseidon.network.proxy.ProxyMessage;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.server.NetHandler;
import net.minecraft.server.NetServerHandler;
import net.minecraft.server.Packet250PluginMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractPlayerConnection implements PlayerConnection, INetworkManager {

    private final AtomicBoolean proxyConnection = new AtomicBoolean(false);
    private final PacketRateLimiter packetRateLimiter = new PacketRateLimiter(this);
    private final PingCalculator pingCalculator = new PingCalculator();

    private final Set<String> channels = ConcurrentHashMap.newKeySet();
    private final Set<String> pendingChannels = new ObjectOpenHashSet<>();
    private boolean sentPendingChannels = false;

    private volatile LoginState loginState = LoginState.INITIAL;

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
        synchronized (this.pendingChannels) {
            if (this.sentPendingChannels) {
                byte[] encodedChannels = StandardMessenger.encodeChannels(channels);
                sendPacket(new Packet250PluginMessage("register", encodedChannels));
            } else {
                this.pendingChannels.addAll(channels);
            }
        }
    }

    public void notifyChannelsUnregistered(Set<String> channels) {
        synchronized (this.pendingChannels) {
            if (this.sentPendingChannels) {
                byte[] encodedChannels = StandardMessenger.encodeChannels(channels);
                sendPacket(new Packet250PluginMessage("unregister", encodedChannels));
            } else {
                this.pendingChannels.removeAll(channels);
            }
        }
    }

    public void sendPendingChannels() {
        synchronized (this.pendingChannels) {
            if (!this.sentPendingChannels) {
                this.sentPendingChannels = true;
                if (!this.pendingChannels.isEmpty()) {
                    byte[] encodedChannels = StandardMessenger.encodeChannels(this.pendingChannels);
                    sendPacket(new Packet250PluginMessage("register", encodedChannels));
                }
            }
        }
    }

    @Override
    public void sendProxyMessage(String tag, byte[] data) {
        Preconditions.checkArgument(tag != null, "tag cannot be null");
        Preconditions.checkArgument(data != null, "data cannot be null");

        if (!isProxyConnection()) {
            String name = Optional.ofNullable(getPlayer()).map(Player::getName).orElse(null);
            throw new UnsupportedOperationException("player " + name + " is not connected through a proxy");
        }
        sendPacket(new ProxyMessage(tag, data));
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

    public void onDetailsReceived(ProxyConnectionDetails details) {
        if (!this.proxyConnection.compareAndSet(false, true)) {
            return;
        }
        setClientAddress(new InetSocketAddress(details.sourceHost(), details.sourcePort()));
    }

    public LoginState getLoginState() {
        return this.loginState;
    }

    public void setLoginState(LoginState newState) {
        Preconditions.checkArgument(newState != null, "newState cannot be null");
        this.loginState = newState;
    }

    public PacketRateLimiter getPacketRateLimiter() {
        return this.packetRateLimiter;
    }

    public PingCalculator getPingCalculator() {
        return this.pingCalculator;
    }
}
