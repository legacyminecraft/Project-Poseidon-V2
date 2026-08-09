package com.legacyminecraft.poseidon.profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.legacyminecraft.poseidon.Poseidon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public final class ProfileCache {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z");
    private static final Type ENTRY_TYPE = new TypeToken<List<ProfileCacheEntry>>() {}.getType();

    private static final Logger log = LoggerFactory.getLogger(ProfileCache.class);
    private static final Gson gson;

    private final File file = new File("usercache.json");
    private final Map<String, ProfileCacheEntry> profilesByName = new ConcurrentHashMap<>();
    private final Map<UUID, ProfileCacheEntry> profilesById = new ConcurrentHashMap<>();
    private final ReentrantLock stateLock = new ReentrantLock();

    public void addProfile(MinecraftProfile profile) {
        ZonedDateTime expiration = ZonedDateTime.now().plusNanos(Poseidon.getConfig().profiles.invalidateCachedProfilesAfter.getNanos());
        ProfileCacheEntry entry = new ProfileCacheEntry(profile, expiration);
        internalAdd(entry);
    }

    private void internalAdd(ProfileCacheEntry entry) {
        try {
            this.stateLock.lock();
            this.profilesByName.put(entry.profile().name().toLowerCase(Locale.ROOT), entry);
            this.profilesById.put(entry.profile().id(), entry);
        } finally {
            this.stateLock.unlock();
        }
    }

    public Optional<MinecraftProfile> getProfile(String name) {
        return getProfile(name, false);
    }

    public Optional<MinecraftProfile> getProfile(String name, boolean removeExpired) {
        try {
            this.stateLock.lock();
            String lowerName = name.toLowerCase(Locale.ROOT);
            ProfileCacheEntry entry = this.profilesByName.get(lowerName);
            if (removeExpired && entry != null && ZonedDateTime.now().isAfter(entry.expiration())) {
                this.profilesByName.remove(lowerName);
                this.profilesById.remove(entry.profile().id());
                entry = null;
            }

            return Optional.ofNullable(entry).map(ProfileCacheEntry::profile);
        } finally {
            this.stateLock.unlock();
        }
    }

    public Optional<MinecraftProfile> getProfile(UUID id) {
        try {
            this.stateLock.lock();
            ProfileCacheEntry entry = this.profilesById.get(id);
            return Optional.ofNullable(entry).map(ProfileCacheEntry::profile);
        } finally {
            this.stateLock.unlock();
        }
    }

    private List<ProfileCacheEntry> getEntries() {
        try {
            this.stateLock.lock();
            return this.profilesById.values().stream().toList();
        } finally {
            this.stateLock.unlock();
        }
    }

    public void load() {
        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            List<ProfileCacheEntry> entries = gson.fromJson(reader, ENTRY_TYPE);
            for (ProfileCacheEntry entry : entries) {
                internalAdd(entry);
            }
        } catch (NoSuchFileException _) {
        } catch (JsonSyntaxException | NullPointerException e) {
            log.warn("Profile cache is corrupted or has bad formatting. Deleting it to prevent further issues.", e);
            this.file.delete();
        } catch (Exception e) {
            log.error("Failed to load profile cache", e);
        }
    }

    public void save() {
        log.info("Saving profile cache");
        List<ProfileCacheEntry> entries = getEntries();
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            gson.toJson(entries, ENTRY_TYPE, writer);
        } catch (Exception e) {
            log.error("Failed to save profile cache", e);
        }
    }

    private record ProfileCacheEntry(MinecraftProfile profile, ZonedDateTime expiration) {

        private static final class Serializer implements JsonDeserializer<ProfileCacheEntry>, JsonSerializer<ProfileCacheEntry> {
            @Override
            public ProfileCacheEntry deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
                try {
                    JsonObject object = element.getAsJsonObject();
                    UUID uuid = UUID.fromString(object.get("uuid").getAsString());
                    String name = object.get("name").getAsString();
                    boolean online = object.get("online").getAsBoolean();
                    ZonedDateTime expiresOn = ZonedDateTime.from(DATE_FORMAT.parse(object.get("expiresOn").getAsString()));
                    return new ProfileCacheEntry(new MinecraftProfile(uuid, name, online), expiresOn);
                } catch (Exception e) {
                    throw new JsonSyntaxException("Failed to deserialize profile cache entry", e);
                }
            }

            @Override
            public JsonObject serialize(ProfileCacheEntry entry, Type type, JsonSerializationContext context) {
                JsonObject object = new JsonObject();
                object.addProperty("uuid", entry.profile().id().toString());
                object.addProperty("name", entry.profile().name());
                object.addProperty("online", entry.profile().online());
                object.addProperty("expiresOn", DATE_FORMAT.format(entry.expiration()));
                return object;
            }
        }
    }

    static {
        gson = new GsonBuilder()
                .disableHtmlEscaping()
                .registerTypeHierarchyAdapter(ProfileCacheEntry.class, new ProfileCacheEntry.Serializer())
                .create();
    }
}
