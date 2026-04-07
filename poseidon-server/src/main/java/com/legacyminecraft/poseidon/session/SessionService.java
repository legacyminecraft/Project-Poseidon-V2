package com.legacyminecraft.poseidon.session;

import com.google.gson.JsonObject;
import com.legacyminecraft.poseidon.Poseidon;
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
        InetAddress finalAddress = ipAddress == null || ipAddress.isLoopbackAddress() ? null : ipAddress;
        URI uri = URI.create(this.hasJoined + "?username=" + name + "&serverId=" + serverId);
        if (finalAddress != null) {
            uri = URI.create(uri + "&ip=" + finalAddress.getHostAddress());
        }

        JsonObject response = this.client.get(uri, JsonObject.class);
        return response != null;
    }

    public static SessionService getInstance() {
        if (instance == null) {
            instance = new SessionService(Poseidon.getServiceClient(), Poseidon.config().services.sessionHost);
        }
        return instance;
    }
}
