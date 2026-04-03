package com.legacyminecraft.poseidon.profile;

import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public final class ProfileRepository {

    private static final int TIMEOUT = 5000;
    private static final int ENTRIES_PER_PAGE = 2;
    private static final int MAX_FAIL_COUNT = 3;
    private static final int DELAY_BETWEEN_PAGES = 100;
    private static final int DELAY_BETWEEN_FAILURES = 750;

    private static final Executor executor;
    private static final Gson gson;

    private static @Nullable ProfileRepository instance;

    private final HttpClient client;
    private final URI lookupByName;
    private final URI lookupById;
    private final URI lookupByNameBulk;

    private ProfileRepository(URI profilesHost) {
        this.client = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(TIMEOUT)).build();
        this.lookupByName = profilesHost.resolve("/minecraft/profile/lookup/name/");
        this.lookupById = profilesHost.resolve("/minecraft/profile/lookup/");
        this.lookupByNameBulk = profilesHost.resolve("/minecraft/profile/lookup/bulk/byname");
    }

    public void lookupProfileByName(String name, ProfileLookupCallback callback) {
        executor.execute(() -> {
            try {
                MinecraftProfile profile = get(this.lookupByName.resolve(name), MinecraftProfile.class);
                callback.onLookupSuccess(profile);
            } catch (ProfileLookupException e) {
                if (e instanceof ProfileLookupHttpException http && http.getResponse().statusCode() == 404) {
                    callback.onLookupFailure(new ProfileNotFoundException());
                } else {
                    callback.onLookupFailure(e);
                }
            }
        });
    }

    public void lookupProfileById(UUID id, ProfileLookupCallback callback) {
        executor.execute(() -> {
            try {
                MinecraftProfile profile = get(this.lookupById.resolve(UuidUtil.toUndashedString(id)), MinecraftProfile.class);
                callback.onLookupSuccess(profile);
            } catch (ProfileLookupException e) {
                if (e instanceof ProfileLookupHttpException http && http.getResponse().statusCode() == 404) {
                    callback.onLookupFailure(new ProfileNotFoundException());
                } else {
                    callback.onLookupFailure(e);
                }
            }
        });
    }

    public void lookupProfilesByNames(Collection<String> names, ProfileLookupCallback callback) {
        executor.execute(() -> {
            Set<String> uniqueNames = names.stream()
                    .map(s -> s.toLowerCase(Locale.ROOT))
                    .collect(Collectors.toSet());

            for (List<String> subList : Iterables.partition(uniqueNames, ENTRIES_PER_PAGE)) {
                JsonArray array = new JsonArray();
                subList.forEach(array::add);
                int failCount = 0;
                boolean failed;

                do {
                    failed = false;

                    try {
                        ProfileBulkLookupResponse response = post(this.lookupByNameBulk, HttpRequest.BodyPublishers.ofString(gson.toJson(array)), ProfileBulkLookupResponse.class);
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
                    } catch (ProfileLookupException e) {
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
        });
    }

    private <T> T get(URI uri, Class<T> responseClass) throws ProfileLookupException {
        return lookup(uri, ProfileLookupMethod.GET, null, responseClass);
    }

    private <T> T post(URI uri, HttpRequest.BodyPublisher bodyPublisher, Class<T> responseClass) throws ProfileLookupException {
        return lookup(uri, ProfileLookupMethod.POST, bodyPublisher, responseClass);
    }

    private <T> T lookup(
            URI uri,
            ProfileLookupMethod method,
            HttpRequest.@Nullable BodyPublisher bodyPublisher,
            Class<T> responseClass) throws ProfileLookupException {
        HttpResponse<String> response;
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofMillis(TIMEOUT));

            HttpRequest request = switch (method) {
                case GET -> builder.GET().build();
                case POST -> builder.POST(bodyPublisher).build();
            };

            response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Throwable e) {
            throw new ProfileLookupException(ProfileLookupException.ErrorType.SERVICE_UNREACHABLE, e);
        }

        int status = response.statusCode();
        if (status < 400) {
            try {
                return gson.fromJson(response.body(), responseClass);
            } catch (JsonParseException e) {
                throw new ProfileLookupException(ProfileLookupException.ErrorType.BAD_RESPONSE, e);
            }
        } else {
            throw new ProfileLookupHttpException(response);
        }
    }

    private static final class MinecraftProfileDeserializer implements JsonDeserializer<MinecraftProfile> {
        @Override
        public MinecraftProfile deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            try {
                JsonObject object = element.getAsJsonObject();
                UUID id = UuidUtil.fromUndashedString(object.get("id").getAsString());
                String name = object.get("name").getAsString();
                return new MinecraftProfile(id, name);
            } catch (Exception e) {
                throw new JsonParseException(e);
            }
        }
    }

    private record ProfileBulkLookupResponse(List<MinecraftProfile> profiles) {
        private static final Type TYPE = new TypeToken<List<MinecraftProfile>>() {}.getType();

        private static final class Deserializer implements JsonDeserializer<ProfileBulkLookupResponse> {
            @Override
            public ProfileBulkLookupResponse deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
                return new ProfileBulkLookupResponse(context.deserialize(element, TYPE));
            }
        }
    }

    public static ProfileRepository getInstance() {
        if (instance == null) {
            instance = new ProfileRepository(URI.create("https://api.minecraftservices.com")); // TODO: make profile host configurable
        }
        return instance;
    }

    static {
        executor = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().name("ProfileRepository-Worker-", 1).factory());
        gson = new GsonBuilder()
                .disableHtmlEscaping()
                .registerTypeAdapter(MinecraftProfile.class, new MinecraftProfileDeserializer())
                .registerTypeAdapter(ProfileBulkLookupResponse.class, new ProfileBulkLookupResponse.Deserializer())
                .create();
    }
}
