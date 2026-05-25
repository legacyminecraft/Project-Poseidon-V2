package com.legacyminecraft.poseidon.network.proxy;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public record ProxyConnectionDetails(
        String sourceHost,
        int sourcePort
) {
    public static final class Serializer implements JsonDeserializer<ProxyConnectionDetails>, JsonSerializer<ProxyConnectionDetails> {
        @Override
        public ProxyConnectionDetails deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            try {
                JsonObject object = element.getAsJsonObject();
                String sourceHost = object.get("sourceHost").getAsString();
                int sourcePort = object.get("sourcePort").getAsInt();
                return new ProxyConnectionDetails(sourceHost, sourcePort);
            } catch (Exception e) {
                throw new JsonParseException("Failed to deserialize proxy connection details", e);
            }
        }

        @Override
        public JsonElement serialize(ProxyConnectionDetails details, Type type, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("sourceHost", details.sourceHost());
            object.addProperty("sourcePort", details.sourcePort());
            return object;
        }
    }
}
