package org.bukkit.craftbukkit.command.defaults;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Comparator;

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
        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();

        // Poseidon start
        Arrays.sort(plugins, Comparator.comparing(plugin -> plugin.getDescription().getFullName()));
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

        return "Plugins (" + enabled + "/" + plugins.length + "): " + pluginList; // Poseidon - change message
    }
}
