package com.legacyminecraft.poseidon.network.ping;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.legacyminecraft.poseidon.Poseidon;
import com.legacyminecraft.poseidon.event.network.ServerListPingEvent;
import com.legacyminecraft.poseidon.network.protocol.ProtocolUtil;
import com.legacyminecraft.poseidon.profile.PlayerProfile;
import com.legacyminecraft.poseidon.util.InternalBukkitAccess;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public final class ServerListPingHandler {

    private static final Random RANDOM = new Random();
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    private final InetSocketAddress clientAddress;
    private int protocolVersion;
    private @Nullable String address;
    private int port;
    private boolean receivedHandshake;
    private boolean closed;

    public ServerListPingHandler(InetSocketAddress clientAddress) {
        this.clientAddress = clientAddress;
    }

    public void handlePing(DataInput input, DataOutput output) throws IOException {
        int length = ProtocolUtil.readVarInt(input);
        int id = ProtocolUtil.readVarInt(input);
        if (id == 0) {
            if (length != 1) {
                this.protocolVersion = ProtocolUtil.readVarInt(input);
                this.address = ProtocolUtil.readUtf8String(input);
                this.port = input.readUnsignedShort();
                ProtocolUtil.readVarInt(input);
                this.receivedHandshake = true;
            } else {
                sendStatusResponse(output);
            }
        } else if (id == 1) {
            long pingId = input.readLong();
            sendPongResponse(pingId, output);
        }
    }

    public boolean isClosed() {
        return this.closed;
    }

    private void sendStatusResponse(DataOutput output) throws IOException {
        Preconditions.checkState(this.receivedHandshake, "handshake has not been received");

        Player[] onlinePlayers;
        try {
            onlinePlayers = getOnlinePlayers().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        ServerListPingEvent event = new ServerListPingEvent(
                this.clientAddress,
                this.protocolVersion,
                InetSocketAddress.createUnresolved(this.address, this.port),
                "b1.7.3",
                this.protocolVersion,
                onlinePlayers.length,
                Bukkit.getMaxPlayers(),
                Bukkit.getMotd(),
                Bukkit.getServerIcon());

        if (Poseidon.getConfig().network.pingProtocol.sendPlayerSample) {
            event.getPlayerSample().addAll(createSample(onlinePlayers));
        }
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            this.closed = true;
            return;
        }

        String response = buildResponse(event);
        ByteArrayDataOutput data = ByteStreams.newDataOutput();
        ProtocolUtil.writeVarInt(0, data);
        ProtocolUtil.writeUtf8String(response, data);
        byte[] bytes = data.toByteArray();

        ProtocolUtil.writeVarInt(bytes.length, output);
        output.write(bytes);
    }

    private void sendPongResponse(long pingId, DataOutput output) throws IOException {
        ByteArrayDataOutput data = ByteStreams.newDataOutput();
        ProtocolUtil.writeVarInt(1, data);
        data.writeLong(pingId);
        byte[] bytes = data.toByteArray();

        ProtocolUtil.writeVarInt(bytes.length, output);
        output.write(bytes);
        this.closed = true;
    }

    private static CompletableFuture<Player[]> getOnlinePlayers() {
        CompletableFuture<Player[]> future = new CompletableFuture<>();
        Bukkit.getScheduler().scheduleSyncDelayedTask(InternalBukkitAccess.INSTANCE, () ->
                future.complete(Bukkit.getOnlinePlayers()));
        return future;
    }

    private static List<PlayerProfile> createSample(Player[] onlinePlayers) {
        int sampleSize = Math.min(onlinePlayers.length, Poseidon.getConfig().network.pingProtocol.maxSampleSize);
        List<PlayerProfile> sample = new ObjectArrayList<>();
        int offset = RANDOM.nextInt(onlinePlayers.length - sampleSize + 1);

        for (int i = 0; i < sampleSize; i++) {
            Player player = onlinePlayers[offset + i];
            sample.add(player.getPlayerProfile());
        }

        Collections.shuffle(sample);
        return sample;
    }

    private static String buildResponse(ServerListPingEvent event) {
        JsonObject response = new JsonObject();

        JsonObject version = new JsonObject();
        version.addProperty("protocol", event.getProtocolVersion());
        version.addProperty("name", event.getVersion());
        response.add("version", version);

        if (!event.shouldHidePlayers()) {
            JsonObject players = new JsonObject();
            players.addProperty("online", event.getNumPlayers());
            players.addProperty("max", event.getMaxPlayers());

            JsonArray sample = new JsonArray();
            event.getPlayerSample().forEach(profile -> {
                JsonObject player = new JsonObject();
                player.addProperty("name", profile.getName());
                player.addProperty("id", profile.getUniqueId().toString());
                sample.add(player);
            });

            players.add("sample", sample);
            response.add("players", players);
        }

        JsonObject description = new JsonObject();
        description.addProperty("text", event.getMotd());
        response.add("description", description);

        if (event.getServerIcon() != null) {
            response.addProperty("favicon", event.getServerIcon().asBase64String());
        }

        return GSON.toJson(response);
    }
}
