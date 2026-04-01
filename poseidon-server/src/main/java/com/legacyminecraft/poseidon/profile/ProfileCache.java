package com.legacyminecraft.poseidon.profile;

import com.google.common.reflect.TypeToken;
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
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

    private static @Nullable ProfileCache instance;
    private static final Logger log = LoggerFactory.getLogger("Minecraft");
    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z");
    private static final Type type = new TypeToken<List<ProfileCacheEntry>>() {}.getType();
    private static final Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .registerTypeHierarchyAdapter(ProfileCacheEntry.class, new ProfileCacheEntrySerializer())
            .create();

    private final File file = new File("usercache.json");
    private final Map<String, ProfileCacheEntry> profilesByName = new ConcurrentHashMap<>();
    private final Map<UUID, ProfileCacheEntry> profilesById = new ConcurrentHashMap<>();
    private final ReentrantLock stateLock = new ReentrantLock();

    public void addProfile(NameAndId nameAndId) {
        ZonedDateTime expiration = ZonedDateTime.now().plusDays(30); // TODO: make expiration configurable
        ProfileCacheEntry entry = new ProfileCacheEntry(nameAndId, expiration);
        internalAdd(entry);
    }

    private void internalAdd(ProfileCacheEntry entry) {
        try {
            this.stateLock.lock();
            this.profilesByName.put(entry.nameAndId().name().toLowerCase(Locale.ROOT), entry);
            this.profilesById.put(entry.nameAndId().id(), entry);
        } finally {
            this.stateLock.unlock();
        }
    }

    public Optional<NameAndId> getProfileIfCached(String name) {
        try {
            this.stateLock.lock();
            ProfileCacheEntry entry = this.profilesByName.get(name.toLowerCase(Locale.ROOT));
            return Optional.ofNullable(entry).map(ProfileCacheEntry::nameAndId);
        } finally {
            this.stateLock.unlock();
        }
    }

    public Optional<NameAndId> getProfile(String name) {
        ProfileCacheEntry entry;
        try {
            this.stateLock.lock();
            String lowerName = name.toLowerCase(Locale.ROOT);
            entry = this.profilesByName.get(lowerName);
            if (entry != null && ZonedDateTime.now().isAfter(entry.expiration())) {
                this.profilesByName.remove(lowerName);
                this.profilesById.remove(entry.nameAndId().id());
                entry = null;
            }
        } finally {
            this.stateLock.unlock();
        }

        if (entry != null) {
            return Optional.of(entry.nameAndId());
        } else {
            Optional<NameAndId> nameAndId = fetchProfile(name);
            nameAndId.ifPresent(this::addProfile);
            return nameAndId;
        }
    }

    public Optional<NameAndId> getProfile(UUID id) {
        try {
            this.stateLock.lock();
            ProfileCacheEntry entry = this.profilesById.get(id);
            return Optional.ofNullable(entry).map(ProfileCacheEntry::nameAndId);
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

    // TODO: implement fetching profiles from api
    private Optional<NameAndId> fetchProfile(String name) {
        String lowerName = name.toLowerCase(Locale.ROOT);
        UUID id = UUID.nameUUIDFromBytes(("OfflinePlayer:" + lowerName).getBytes(StandardCharsets.UTF_8));
        return Optional.of(new NameAndId(name, id));
    }

    public void load() {
        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            List<ProfileCacheEntry> entries = gson.fromJson(reader, type);
            for (ProfileCacheEntry entry : entries) {
                internalAdd(entry);
            }
        } catch (FileNotFoundException _) {
        } catch (JsonSyntaxException | NullPointerException e) {
            log.warn("Profile cache is corrupted or has bad formatting. Deleting it to prevent further issues.", e);
            this.file.delete();
        } catch (JsonParseException | IOException e) {
            log.error("Failed to load profile cache", e);
        }
    }

    public void save() {
        List<ProfileCacheEntry> entries = getEntries();
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            gson.toJson(entries, type, writer);
        } catch (Exception e) {
            log.error("Failed to save profile cache", e);
        }
    }

    private static final class ProfileCacheEntrySerializer
            implements JsonDeserializer<ProfileCacheEntry>, JsonSerializer<ProfileCacheEntry> {

        @Override
        public @Nullable ProfileCacheEntry deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            if (!element.isJsonObject()) {
                return null;
            }

            JsonObject object = element.getAsJsonObject();
            try {
                UUID uuid = UUID.fromString(object.get("uuid").getAsString());
                String name = object.get("name").getAsString();
                ZonedDateTime expiresOn = ZonedDateTime.from(format.parse(object.get("expiresOn").getAsString()));
                return new ProfileCacheEntry(new NameAndId(name, uuid), expiresOn);
            } catch (Exception e) {
                log.warn("Failed to deserialize profile cache entry", e);
                return null;
            }
        }

        @Override
        public JsonObject serialize(ProfileCacheEntry entry, Type type, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("uuid", entry.nameAndId().id().toString());
            object.addProperty("name", entry.nameAndId().name());
            object.addProperty("expiresOn", format.format(entry.expiration()));
            return object;
        }
    }

    public static ProfileCache getInstance() {
        if (instance == null) {
            instance = new ProfileCache();
        }
        return instance;
    }
}
