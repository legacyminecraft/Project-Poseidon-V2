package com.legacyminecraft.poseidon.util;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.Main;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.util.config.Configuration;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.logging.Logger;

public final class InternalBukkitAccess implements Plugin {

    public static final InternalBukkitAccess INSTANCE = new InternalBukkitAccess();

    private final PluginLoader pluginLoader;
    private final PluginDescriptionFile description;

    private InternalBukkitAccess() {
        this.pluginLoader = new JavaPluginLoader(Bukkit.getServer());
        this.description = new PluginDescriptionFile("InternalBukkitAccess", Bukkit.getVersion(), Main.class.getName());
    }

    @Override
    public File getDataFolder() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PluginDescriptionFile getDescription() {
        return this.description;
    }

    @Override
    public Configuration getConfiguration() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PluginLoader getPluginLoader() {
        return this.pluginLoader;
    }

    @Override
    public Server getServer() {
        return Bukkit.getServer();
    }

    @Override
    public Logger getLogger() {
        return Bukkit.getServer().getLogger();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
    }

    @Override
    public boolean isNaggable() {
        return false;
    }

    @Override
    public void setNaggable(boolean canNag) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @Nullable ChunkGenerator getDefaultWorldGenerator(String worldName, @Nullable String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        throw new UnsupportedOperationException();
    }
}
