package com.legacyminecraft.poseidon.profile;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.UUID;

public final class MinecraftProfileDeserializer implements JsonDeserializer<MinecraftProfile> {

    @Override
    public MinecraftProfile deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        try {
            JsonObject object = element.getAsJsonObject();
            UUID id = UuidUtil.fromUndashedString(object.get("id").getAsString());
            String name = object.get("name").getAsString();
            return new MinecraftProfile(id, name, true);
        } catch (Exception e) {
            throw new JsonParseException(e);
        }
    }
}
