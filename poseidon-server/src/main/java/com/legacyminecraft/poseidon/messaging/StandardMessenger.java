package com.legacyminecraft.poseidon.messaging;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.legacyminecraft.poseidon.network.connection.AbstractPlayerConnection;
import com.legacyminecraft.poseidon.network.connection.ConnectionManager;
import com.legacyminecraft.poseidon.network.connection.PlayerConnection;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.bukkit.plugin.Plugin;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class StandardMessenger implements Messenger {

    public static final String REGISTER_CHANNEL = "register";
    public static final String UNREGISTER_CHANNEL = "unregister";
    public static final String PROXY_HELLO_CHANNEL = Messenger.PROXY_CHANNEL_PREFIX + "hello";

    private final ConnectionManager<? extends AbstractPlayerConnection> connectionManager;
    private final Map<String, PluginMessageListenerRegistration> inboundByChannel = new Object2ObjectOpenHashMap<>();
    private final Map<Plugin, Set<PluginMessageListenerRegistration>> inboundByPlugin = new Object2ObjectOpenHashMap<>();
    private final Map<String, Plugin> outboundByChannel = new Object2ObjectOpenHashMap<>();
    private final Map<Plugin, Set<String>> outboundByPlugin = new Object2ObjectOpenHashMap<>();
    private final Object inboundLock = new Object();
    private final Object outboundLock = new Object();

    public StandardMessenger(ConnectionManager<? extends AbstractPlayerConnection> connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public boolean registerInboundChannel(Plugin owningPlugin, String channel, PluginMessageListener listener) {
        Preconditions.checkArgument(owningPlugin != null, "owningPlugin cannot be null");
        validateChannel(channel);
        Preconditions.checkArgument(listener != null, "listener cannot be null");

        synchronized (this.inboundLock) {
            PluginMessageListenerRegistration registration = new PluginMessageListenerRegistration(this, owningPlugin, channel, listener);
            if (this.inboundByChannel.putIfAbsent(channel, registration) == null) {
                this.inboundByPlugin.computeIfAbsent(owningPlugin, _ -> new ObjectOpenHashSet<>()).add(registration);
                notifyChannelsRegistered(Set.of(channel));
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean registerOutboundChannel(Plugin owningPlugin, String channel) {
        Preconditions.checkArgument(owningPlugin != null, "owningPlugin cannot be null");
        validateChannel(channel);

        synchronized (this.outboundLock) {
            if (this.outboundByChannel.putIfAbsent(channel, owningPlugin) == null) {
                this.outboundByPlugin.computeIfAbsent(owningPlugin, _ -> new ObjectOpenHashSet<>()).add(channel);
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean unregisterInboundChannel(Plugin owningPlugin, String channel) {
        Preconditions.checkArgument(owningPlugin != null, "owningPlugin cannot be null");
        validateChannel(channel);

        synchronized (this.inboundLock) {
            PluginMessageListenerRegistration registration = this.inboundByChannel.get(channel);
            if (registration != null && registration.owningPlugin().equals(owningPlugin)) {
                this.inboundByChannel.remove(channel);
                this.inboundByPlugin.get(owningPlugin).remove(registration);
                notifyChannelsUnregistered(Set.of(channel));
                return true;
            }
            return false;
        }
    }

    @Override
    public void unregisterInboundChannels(Plugin owningPlugin) {
        Preconditions.checkArgument(owningPlugin != null, "owningPlugin cannot be null");

        synchronized (this.inboundLock) {
            Optional.ofNullable(this.inboundByPlugin.remove(owningPlugin))
                    .ifPresent(registrations -> {
                        Set<String> channels = registrations.stream()
                                .map(PluginMessageListenerRegistration::channel)
                                .collect(Collectors.toSet());
                        channels.forEach(this.inboundByChannel::remove);
                        notifyChannelsUnregistered(channels);
                    });
        }
    }

    @Override
    public boolean unregisterOutboundChannel(Plugin owningPlugin, String channel) {
        Preconditions.checkArgument(owningPlugin != null, "owningPlugin cannot be null");
        validateChannel(channel);

        synchronized (this.outboundLock) {
            Plugin plugin = this.outboundByChannel.get(channel);
            if (plugin != null && plugin.equals(owningPlugin)) {
                this.outboundByChannel.remove(channel);
                this.outboundByPlugin.get(owningPlugin).remove(channel);
                return true;
            }
            return false;
        }
    }

    @Override
    public void unregisterOutboundChannels(Plugin owningPlugin) {
        Preconditions.checkArgument(owningPlugin != null, "owningPlugin cannot be null");

        synchronized (this.outboundLock) {
            Optional.ofNullable(this.outboundByPlugin.remove(owningPlugin))
                    .ifPresent(channels -> channels.forEach(this.outboundByChannel::remove));
        }
    }

    @Override
    public boolean isInboundChannelRegistered(Plugin owningPlugin, String channel) {
        Preconditions.checkArgument(owningPlugin != null, "owningPlugin cannot be null");
        validateChannel(channel);

        synchronized (this.inboundLock) {
            return Optional.ofNullable(this.inboundByChannel.get(channel))
                    .map(PluginMessageListenerRegistration::owningPlugin)
                    .map(plugin -> plugin.equals(owningPlugin))
                    .orElse(false);
        }
    }

    @Override
    public boolean isOutboundChannelRegistered(Plugin owningPlugin, String channel) {
        Preconditions.checkArgument(owningPlugin != null, "owningPlugin cannot be null");
        validateChannel(channel);

        synchronized (this.outboundLock) {
            return Optional.ofNullable(this.outboundByChannel.get(channel))
                    .map(plugin -> plugin.equals(owningPlugin))
                    .orElse(false);
        }
    }

    @Override
    public Set<String> getInboundChannels() {
        synchronized (this.inboundLock) {
            return Set.copyOf(this.inboundByChannel.keySet());
        }
    }

    @Override
    public Set<String> getInboundChannels(Plugin owningPlugin) {
        Preconditions.checkArgument(owningPlugin != null, "owningPlugin cannot be null");

        synchronized (this.inboundLock) {
            return Optional.ofNullable(this.inboundByPlugin.get(owningPlugin))
                    .map(registrations -> registrations.stream()
                            .map(PluginMessageListenerRegistration::channel)
                            .collect(Collectors.toSet())
                    )
                    .orElse(Set.of());
        }
    }

    @Override
    public Set<String> getOutboundChannels() {
        synchronized (this.outboundLock) {
            return Set.copyOf(this.outboundByChannel.keySet());
        }
    }

    @Override
    public Set<String> getOutboundChannels(Plugin owningPlugin) {
        Preconditions.checkArgument(owningPlugin != null, "owningPlugin cannot be null");

        synchronized (this.outboundLock) {
            return Optional.ofNullable(this.outboundByPlugin.get(owningPlugin))
                    .map(Set::copyOf)
                    .orElse(Set.of());
        }
    }

    @Override
    public void dispatchInboundMessage(PlayerConnection source, String channel, byte[] message) {
        Preconditions.checkArgument(source != null, "source cannot be null");
        validateChannel(channel);
        validateMessage(message);

        synchronized (this.inboundLock) {
            Optional.ofNullable(this.inboundByChannel.get(channel))
                    .map(PluginMessageListenerRegistration::listener)
                    .ifPresent(listener -> listener.onPluginMessageReceived(source, channel, message));
        }
    }

    public void notifyChannelsRegistered(Set<String> channels) {
        this.connectionManager.getConnections().forEach(connection -> connection.notifyChannelsRegistered(channels));
    }

    public void notifyChannelsUnregistered(Set<String> channels) {
        this.connectionManager.getConnections().forEach(connection -> connection.notifyChannelsUnregistered(channels));
    }

    public static byte[] encodeChannels(Set<String> channels) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        for (String channel : channels) {
            output.write(channel.getBytes(StandardCharsets.UTF_8));
            output.write((byte) 0);
        }
        return output.toByteArray();
    }

    public static Set<String> decodeChannels(byte[] message) {
        String channels = new String(message, StandardCharsets.UTF_8);
        return Arrays.stream(channels.split("\0")).collect(Collectors.toSet());
    }

    public static void validatePluginMessage(Messenger messenger, Plugin owningPlugin, String channel, byte[] message) {
        Preconditions.checkArgument(messenger != null, "messenger cannot be null");
        Preconditions.checkArgument(owningPlugin != null, "owningPlugin cannot be null");
        validateChannel(channel);
        validateMessage(message);
        Preconditions.checkState(owningPlugin.isEnabled(), "plugin must be enabled to send plugin messages");
        Preconditions.checkState(messenger.isOutboundChannelRegistered(owningPlugin, channel),
                "outbound channel '" + channel + "' is not registered or plugin '"
                + owningPlugin.getDescription().getName() + "' does not own the channel");
    }

    public static void validateChannel(String channel) {
        Preconditions.checkArgument(channel != null, "channel cannot be null");
        Preconditions.checkArgument(channel.length() <= MAX_CHANNEL_LENGTH,
                "channel name cannot be longer than " + MAX_CHANNEL_LENGTH + " characters");
        Preconditions.checkArgument(!isRestrictedChannel(channel), "'" + channel + "' cannot be used as a channel name");
    }

    public static void validateMessage(byte[] message) {
        Preconditions.checkArgument(message != null, "message cannot be null");
        Preconditions.checkArgument(message.length <= MAX_MESSAGE_LENGTH,
                "message cannot be longer than " + MAX_MESSAGE_LENGTH + " characters");
    }

    public static boolean isRestrictedChannel(String channel) {
        Preconditions.checkArgument(channel != null, "channel cannot be null");
        return channel.equals(REGISTER_CHANNEL) || channel.equals(UNREGISTER_CHANNEL);
    }

    public static boolean isProxyChannel(String channel) {
        Preconditions.checkArgument(channel != null, "channel cannot be null");
        return channel.startsWith(Messenger.PROXY_CHANNEL_PREFIX);
    }
}
