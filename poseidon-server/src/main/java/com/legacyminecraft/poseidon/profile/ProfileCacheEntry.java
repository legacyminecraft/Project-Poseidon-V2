package com.legacyminecraft.poseidon.profile;

import java.time.ZonedDateTime;

public record ProfileCacheEntry(NameAndId nameAndId, ZonedDateTime expiration) {
}
