package com.legacyminecraft.poseidon.network.connection;

import com.google.common.base.Preconditions;
import com.legacyminecraft.poseidon.event.messaging.PlayerRegisterChannelEvent;
import com.legacyminecraft.poseidon.event.messaging.PlayerUnregisterChannelEvent;
import com.legacyminecraft.poseidon.messaging.StandardMessenger;
import com.legacyminecraft.poseidon.network.login.LoginState;
import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import com.legacyminecraft.poseidon.network.proxy.ProxyConnectionDetails;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class AbstractPlayerConnection implements PlayerConnection, INetworkManager {

    private final AtomicBoolean proxyConnection = new AtomicBoolean(false);
    private final AtomicBoolean clientSupportsMessaging = new AtomicBoolean(false);
    private final Set<String> channels = ConcurrentHashMap.newKeySet();
    private final PacketRateLimiter packetRateLimiter = new PacketRateLimiter(this);
    private final PingCalculator pingCalculator = new PingCalculator();

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
        notifyChannels(channels, true);
    }

    public void notifyChannelsUnregistered(Set<String> channels) {
        notifyChannels(channels, false);
    }

    private void notifyChannels(Set<String> channels, boolean register) {
        Set<String> proxyChannels = new ObjectOpenHashSet<>();
        Set<String> clientChannels = new ObjectOpenHashSet<>();

        channels.forEach(channel -> {
            if (StandardMessenger.isProxyChannel(channel)) {
                proxyChannels.add(channel);
            } else {
                clientChannels.add(channel);
            }
        });

        String channel = register ? StandardMessenger.REGISTER_CHANNEL : StandardMessenger.UNREGISTER_CHANNEL;

        if (!proxyChannels.isEmpty() && this.proxyConnection.get()) {
            byte[] encodedChannels = StandardMessenger.encodeChannels(proxyChannels);
            sendPacket(new Packet250PluginMessage(channel, encodedChannels));
        }

        if (!clientChannels.isEmpty() && this.clientSupportsMessaging.get()) {
            byte[] encodedChannels = StandardMessenger.encodeChannels(clientChannels);
            sendPacket(new Packet250PluginMessage(channel, encodedChannels));
        }
    }

    public void sendClientChannels() {
        if (this.clientSupportsMessaging.compareAndSet(false, true)) {
            Set<String> clientChannels = Bukkit.getMessenger().getInboundChannels().stream()
                    .filter(Predicate.not(StandardMessenger::isProxyChannel))
                    .collect(Collectors.toSet());

            if (!clientChannels.isEmpty()) {
                byte[] encodedChannels = StandardMessenger.encodeChannels(clientChannels);
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
            setClientAddress(new InetSocketAddress(details.sourceHost(), details.sourcePort()));
            Set<String> proxyChannels = Bukkit.getMessenger().getInboundChannels().stream()
                    .filter(StandardMessenger::isProxyChannel)
                    .collect(Collectors.toSet());

            if (!proxyChannels.isEmpty()) {
                byte[] encodedChannels = StandardMessenger.encodeChannels(proxyChannels);
                sendPacket(new Packet250PluginMessage(StandardMessenger.REGISTER_CHANNEL, encodedChannels));
            }
        }
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
