package com.legacyminecraft.poseidon.profile;

import com.google.common.collect.Iterables;
import com.google.gson.JsonArray;
import com.legacyminecraft.poseidon.Poseidon;
import com.legacyminecraft.poseidon.service.ServiceClient;
import com.legacyminecraft.poseidon.service.ServiceClientException;
import com.legacyminecraft.poseidon.service.ServiceClientHttpException;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class ProfileService {

    private static final int ENTRIES_PER_PAGE = 2;
    private static final int MAX_FAIL_COUNT = 3;
    private static final int DELAY_BETWEEN_PAGES = 100;
    private static final int DELAY_BETWEEN_FAILURES = 750;

    private static @Nullable ProfileService instance;

    private final ServiceClient client;
    private final URI lookupByName;
    private final URI lookupById;
    private final URI lookupByNameBulk;

    private ProfileService(ServiceClient client, URI profileHost) {
        this.client = client;
        this.lookupByName = profileHost.resolve("/minecraft/profile/lookup/name/");
        this.lookupById = profileHost.resolve("/minecraft/profile/lookup/");
        this.lookupByNameBulk = profileHost.resolve("/minecraft/profile/lookup/bulk/byname");
    }

    public MinecraftProfile lookupProfileByName(String name) throws ProfileNotFoundException, ServiceClientException {
        try {
            return this.client.get(this.lookupByName.resolve(name), MinecraftProfile.class);
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
            return this.client.get(this.lookupById.resolve(UuidUtil.toUndashedString(id)), MinecraftProfile.class);
        } catch (ServiceClientException e) {
            if (e instanceof ServiceClientHttpException http && http.getResponse().statusCode() == 404) {
                throw new ProfileNotFoundException();
            } else {
                throw e;
            }
        }
    }

    public void lookupProfilesByNames(Collection<String> names, ProfileLookupCallback callback) {
        Set<String> uniqueNames = names.stream()
                .map(s -> s.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());

        for (List<String> subList : Iterables.partition(uniqueNames, ENTRIES_PER_PAGE)) {
            JsonArray array = new JsonArray();
            subList.forEach(array::add);
            HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(array.toString());
            int failCount = 0;
            boolean failed;

            do {
                failed = false;

                try {
                    ProfileBulkLookupResponse response = this.client.post(this.lookupByNameBulk, body, ProfileBulkLookupResponse.class);
                    failCount = 0;

                    Set<String> received = new HashSet<>();
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

    public static ProfileService getInstance() {
        if (instance == null) {
            instance = new ProfileService(Poseidon.getServiceClient(), Poseidon.config().services.profileHost);
        }
        return instance;
    }
}
