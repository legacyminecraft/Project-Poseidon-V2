package com.legacyminecraft.poseidon.profile;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public record ProfileBulkLookupResponse(List<MinecraftProfile> profiles) {
    private static final Type TYPE = new TypeToken<List<MinecraftProfile>>() {}.getType();

    public static final class Deserializer implements JsonDeserializer<ProfileBulkLookupResponse> {
        @Override
        public ProfileBulkLookupResponse deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            return new ProfileBulkLookupResponse(context.deserialize(element, TYPE));
        }
    }
}
