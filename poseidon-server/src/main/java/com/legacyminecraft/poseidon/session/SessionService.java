package com.legacyminecraft.poseidon.session;

import com.google.gson.JsonObject;
import com.legacyminecraft.poseidon.Poseidon;
import com.legacyminecraft.poseidon.service.ServiceClient;
import com.legacyminecraft.poseidon.service.ServiceClientException;
import org.jspecify.annotations.Nullable;

import java.net.InetAddress;

public final class SessionService {

    private final ServiceClient client;

    public SessionService(ServiceClient client) {
        this.client = client;
    }

    public boolean verifySession(String name, String serverId, @Nullable InetAddress ipAddress) throws ServiceClientException {
        String url = getSessionHost() + "/session/minecraft/hasJoined?username=" + name + "&serverId=" + serverId;
        InetAddress finalAddress = ipAddress == null || ipAddress.isLoopbackAddress() ? null : ipAddress;
        if (finalAddress != null) {
            url += "&ip=" + finalAddress.getHostAddress();
        }

        JsonObject response = this.client.get(url, JsonObject.class);
        return response != null;
    }

    private String getSessionHost() {
        return Poseidon.getConfig().services.sessionHost;
    }
}
