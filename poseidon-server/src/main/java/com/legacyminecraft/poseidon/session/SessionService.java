package com.legacyminecraft.poseidon.session;

import com.google.gson.JsonObject;
import com.legacyminecraft.poseidon.service.ServiceClient;
import com.legacyminecraft.poseidon.service.ServiceClientException;
import org.jspecify.annotations.Nullable;

import java.net.InetAddress;
import java.net.URI;

public final class SessionService {

    private static @Nullable SessionService instance;

    private final ServiceClient client;
    private final URI hasJoined;

    public SessionService(ServiceClient client, URI sessionHost) {
        this.client = client;
        this.hasJoined = sessionHost.resolve("/session/minecraft/hasJoined");
    }

    public boolean verifySession(String name, String serverId, @Nullable InetAddress ipAddress) throws ServiceClientException {
        URI uri = this.hasJoined.resolve("?username=").resolve(name).resolve("&serverId=").resolve(serverId);
        if (ipAddress != null) {
            uri = uri.resolve("&ip=").resolve(ipAddress.getHostAddress());
        }

        JsonObject response = this.client.get(uri, JsonObject.class);
        return response != null;
    }

    public static SessionService getInstance() {
        if (instance == null) {
            instance = new SessionService(ServiceClient.getInstance(), URI.create("https://sessionserver.mojang.com")); // TODO: make session host configurable
        }
        return instance;
    }
}
