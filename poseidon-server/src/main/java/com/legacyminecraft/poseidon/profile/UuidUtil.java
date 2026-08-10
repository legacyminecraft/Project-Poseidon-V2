package com.legacyminecraft.poseidon.profile;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

public final class UuidUtil {

    private UuidUtil() {
    }

    public static UUID generateOfflineUuid(String name) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name.toLowerCase(Locale.ROOT)).getBytes(StandardCharsets.UTF_8));
    }

    public static UUID generateLegacyOfflineUuid(String name) {
        return UUID.nameUUIDFromBytes(name.getBytes());
    }

    public static UUID fromUndashedString(String string) {
        return UUID.fromString(string.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
    }

    public static String toUndashedString(UUID uuid) {
        return uuid.toString().replace("-", "");
    }
}
