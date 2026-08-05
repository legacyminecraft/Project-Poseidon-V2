package com.legacyminecraft.poseidon.performance.spark;

import me.lucko.spark.common.command.sender.AbstractCommandSender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public final class PoseidonCommandSender extends AbstractCommandSender<CommandSender> {

    public PoseidonCommandSender(CommandSender delegate) {
        super(delegate);
    }

    @Override
    public String getName() {
        return this.delegate.getName();
    }

    @Override
    public @Nullable UUID getUniqueId() {
        if (this.delegate instanceof Player player) {
            return player.getUniqueId();
        }
        return null;
    }

    @Override
    public void sendMessage(Component component) {
        String message = LegacyComponentSerializer.legacySection().serialize(component)
                .replaceAll("§[k-o]", "")
                .replaceAll("§r", "§f")
                .replaceAll("⚡", "spark");

        for (String line : message.split("\n")) {
            this.delegate.sendMessage(line);
        }
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.delegate.hasPermission(permission);
    }
}
