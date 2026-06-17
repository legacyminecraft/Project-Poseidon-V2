package com.legacyminecraft.poseidon.messaging;

import org.bukkit.plugin.Plugin;

public record PluginMessageListenerRegistration(
        Messenger messenger,
        Plugin owningPlugin,
        String channel,
        PluginMessageListener listener
) {
}
