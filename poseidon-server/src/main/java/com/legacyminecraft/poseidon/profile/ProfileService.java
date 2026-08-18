package com.legacyminecraft.poseidon.profile;

import com.google.common.collect.Iterables;
import com.google.gson.JsonArray;
import com.legacyminecraft.poseidon.Poseidon;
import com.legacyminecraft.poseidon.service.ServiceClient;
import com.legacyminecraft.poseidon.service.ServiceClientException;
import com.legacyminecraft.poseidon.service.ServiceClientHttpException;
import com.legacyminecraft.poseidon.util.CaseInsensitiveSet;

import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public final class ProfileService {

    private static final int ENTRIES_PER_PAGE = 2;
    private static final int MAX_FAIL_COUNT = 3;
    private static final int DELAY_BETWEEN_PAGES = 100;
    private static final int DELAY_BETWEEN_FAILURES = 750;

    private final ServiceClient client;

    public ProfileService(ServiceClient client) {
        this.client = client;
    }

    public MinecraftProfile lookupProfileByName(String name) throws ProfileNotFoundException, ServiceClientException {
        try {
            String url = Poseidon.getConfig().profiles.lookupByNameUrl.replace("{name}", encode(name));
            return this.client.get(url, MinecraftProfile.class);
        } catch (ServiceClientException e) {
            if (e instanceof ServiceClientHttpException http &&
                    (http.getResponse().statusCode() == 404 || http.getResponse().statusCode() == 400)) {
                throw new ProfileNotFoundException();
            } else {
                throw e;
            }
        }
    }

    public MinecraftProfile lookupProfileById(UUID id) throws ProfileNotFoundException, ServiceClientException {
        try {
            String url = Poseidon.getConfig().profiles.lookupByIdUrl.replace("{uuid}", encode(UuidUtil.toUndashedString(id)));
            return this.client.get(url, MinecraftProfile.class);
        } catch (ServiceClientException e) {
            if (e instanceof ServiceClientHttpException http && http.getResponse().statusCode() == 404) {
                throw new ProfileNotFoundException();
            } else {
                throw e;
            }
        }
    }

    public void lookupProfilesByNames(Collection<String> names, ProfileLookupCallback callback) {
        String url = Poseidon.getConfig().profiles.lookupBulkByNameUrl;
        for (List<String> subList : Iterables.partition(names, ENTRIES_PER_PAGE)) {
            JsonArray array = new JsonArray();
            subList.forEach(array::add);
            HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(array.toString());
            int failCount = 0;
            boolean failed;

            do {
                failed = false;

                try {
                    ProfileBulkLookupResponse response = this.client.post(url, body, ProfileBulkLookupResponse.class);
                    failCount = 0;

                    Set<String> received = new CaseInsensitiveSet();
                    for (MinecraftProfile profile : response.profiles()) {
                        received.add(profile.name().toLowerCase(Locale.ROOT));
                        callback.onLookupSuccess(profile);
                    }

                    for (String name : subList) {
                        if (!received.contains(name)) {
                            callback.onLookupFailure(new ProfileNotFoundException());
                        }
                    }

                    try {
                        Thread.sleep(DELAY_BETWEEN_PAGES);
                    } catch (InterruptedException _) {
                    }
                } catch (ServiceClientException e) {
                    if (++failCount == MAX_FAIL_COUNT) {
                        for (String _ : subList) {
                            callback.onLookupFailure(e);
                        }
                    } else {
                        try {
                            Thread.sleep(DELAY_BETWEEN_FAILURES);
                        } catch (InterruptedException _) {
                        }
                        failed = true;
                    }
                }
            } while (failed);
        }
    }

    private static String encode(String str) {
        return URLEncoder.encode(str, StandardCharsets.UTF_8);
    }
}
