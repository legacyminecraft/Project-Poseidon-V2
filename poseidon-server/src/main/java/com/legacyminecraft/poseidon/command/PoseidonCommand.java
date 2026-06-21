package com.legacyminecraft.poseidon.command;

import com.legacyminecraft.poseidon.config.PoseidonGlobalConfig;
import com.legacyminecraft.poseidon.config.PoseidonWorldConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.World;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.permissions.DefaultPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class PoseidonCommand extends Command {

    private static final String ROOT_PERMISSION = "poseidon.command.poseidon";
    private static final String RELOAD_PERMISSION = ROOT_PERMISSION + ".reload";
    private static final Logger log = LoggerFactory.getLogger(PoseidonCommand.class);

    private final CommandDispatcher<CommandSender> dispatcher = new CommandDispatcher<>();

    public PoseidonCommand() {
        super("poseidon");
        setDescription("Commands specific to Project Poseidon.");
        setUsage("/poseidon");
        setAliases(List.of("project-poseidon", "projectposeidon"));
        setPermission("poseidon.command.poseidon");
        register();
    }

    private void register() {
        DefaultPermissions.registerPermission(ROOT_PERMISSION, "Allows access to /poseidon", PermissionDefault.OP);
        DefaultPermissions.registerPermission(RELOAD_PERMISSION, "Allows access to /poseidon reload", PermissionDefault.OP);

        this.dispatcher.register(LiteralArgumentBuilder.<CommandSender>literal("poseidon")
                .then(LiteralArgumentBuilder.<CommandSender>literal("version")
                        .executes(this::displayVersion))
                .then(LiteralArgumentBuilder.<CommandSender>literal("reload")
                        .requires(sender -> sender.hasPermission(RELOAD_PERMISSION))
                        .executes(this::reload)
                ));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        try {
            String joinedArgs = args.length == 0 ? "" : " " + String.join(" ", args);
            this.dispatcher.execute(getName() + joinedArgs, sender);
        } catch (CommandSyntaxException e) {
            displayUsage(sender);
        }

        return true;
    }

    private void displayUsage(CommandSender sender) {
        String subcommands = String.join("|", this.dispatcher.getSmartUsage(this.dispatcher.getRoot().getChild("poseidon"), sender).values());
        sender.sendMessage(ChatColor.RED + "Usage: /poseidon (" + subcommands + ")");
    }

    private int displayVersion(CommandContext<CommandSender> context) {
        context.getSource().sendMessage(Bukkit.getVersionString());
        return 1;
    }

    private int reload(CommandContext<CommandSender> context) {
        try {
            PoseidonGlobalConfig.load();
            PoseidonWorldConfig.loadDefaults();
            Bukkit.getWorlds().stream().map(w -> ((CraftWorld) w).getHandle()).forEach(World::reloadConfig);
            context.getSource().sendMessage(ChatColor.GREEN + "Poseidon configuration reloaded.");
        } catch (Throwable e) {
            context.getSource().sendMessage(ChatColor.RED + "An error occurred while reloading the configuration");
            log.error("Failed to reload configuration", e);
        }

        return 1;
    }
}
