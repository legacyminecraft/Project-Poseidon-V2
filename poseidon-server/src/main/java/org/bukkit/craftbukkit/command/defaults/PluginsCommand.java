package org.bukkit.craftbukkit.command.defaults;

import com.legacyminecraft.poseidon.Poseidon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class PluginsCommand extends Command {
    public PluginsCommand(String name) {
        super(name);
        this.description = "Gets a list of plugins running on the server";
        this.usageMessage = "/plugins";
        this.setPermission("bukkit.command.plugins");
        this.setAliases(Arrays.asList("pl"));
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;

        sender.sendMessage(getPluginList()); // Poseidon - change message
        return true;
    }

    private String getPluginList() {
        StringBuilder pluginList = new StringBuilder();
        // Poseidon start
        List<Plugin> plugins = Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .filter(plugin -> !Poseidon.getConfig().plugins.hiddenPlugins.contains(plugin.getDescription().getName()))
                .sorted(Comparator.comparing(plugin -> plugin.getDescription().getName()))
                .toList();
        int enabled = 0;
        // Poseidon end

        for (Plugin plugin : plugins) {
            if (!pluginList.isEmpty()) {
                pluginList.append(ChatColor.WHITE);
                pluginList.append(", ");
            }

            pluginList.append(plugin.isEnabled() ? ChatColor.GREEN : ChatColor.RED);
            pluginList.append(plugin.getDescription().getName());

            // Poseidon start
            if (plugin.isEnabled()) {
                enabled++;
            }
            // Poseidon end
        }

        return "Plugins (" + enabled + "/" + plugins.size() + "): " + pluginList; // Poseidon - change message
    }
}
