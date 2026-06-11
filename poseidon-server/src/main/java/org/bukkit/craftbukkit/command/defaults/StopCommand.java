package org.bukkit.craftbukkit.command.defaults;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StopCommand extends VanillaCommand {
    public StopCommand() {
        super("stop");
        this.description = "Stops the server";
        this.usageMessage = "/stop";
        this.setPermission("bukkit.command.stop");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;

        Command.broadcastCommandMessage(sender, "Stopping the server..");
        Bukkit.shutdown();

        // Poseidon start - kick online players before stopping server
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer("Server closed");
        }
        // Poseidon end

        return true;
    }

    @Override
    public boolean matches(String input) {
        return input.startsWith("stop");
    }
}
