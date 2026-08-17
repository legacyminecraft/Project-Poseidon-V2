package com.legacyminecraft.poseidon.version;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public record GitHubRelease(String tag, String url) {

    public static final class Deserializer implements JsonDeserializer<GitHubRelease> {
        @Override
        public GitHubRelease deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            try {
                JsonObject object = element.getAsJsonObject();
                String tag = object.get("tag_name").getAsString();
                String url = object.get("html_url").getAsString();
                return new GitHubRelease(tag, url);
            } catch (Exception e) {
                throw new JsonParseException(e);
            }
        }
    }
}
