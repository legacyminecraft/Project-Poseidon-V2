package com.legacyminecraft.poseidon.session;

import com.google.gson.JsonObject;
import com.legacyminecraft.poseidon.Poseidon;
import com.legacyminecraft.poseidon.service.ServiceClient;
import com.legacyminecraft.poseidon.service.ServiceClientException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class SessionService {

    private final ServiceClient client;

    public SessionService(ServiceClient client) {
        this.client = client;
    }

    public boolean verifySession(String name, String serverId) throws ServiceClientException {
        // Note: Deliberately omitting the &id= GET parameter as it causes issues
        //       with third party authentication proxies (e.g. Betacraft). Do not add it back!
        String url = Poseidon.getConfig().sessions.verifySessionUrl
                + "?username=" + encode(name)
                + "&serverId=" + encode(serverId);

        JsonObject response = this.client.get(url, JsonObject.class);
        return response != null;
    }

    private static String encode(String str) {
        return URLEncoder.encode(str, StandardCharsets.UTF_8);
    }
}
