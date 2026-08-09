package com.projectposeidon.api;

import com.legacyminecraft.poseidon.profile.PlayerProfile;
import org.bukkit.OfflinePlayer;

/**
 * This class is provided for backward compatibility of plugins using the
 * legacy methods of accessing cached player UUIDs by name and vice versa.
 *
 * @deprecated It is encouraged to use {@link OfflinePlayer#getPlayerProfile()}
 * and {@link PlayerProfile#isOnlineProfile()} to determine whether a player
 * profile is online or offline.
 */
@Deprecated
public enum UUIDType {
    ONLINE,
    OFFLINE,
    UNKNOWN
}
