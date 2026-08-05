package com.legacyminecraft.poseidon.command;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.command.defaults.BanCommand;
import org.bukkit.craftbukkit.command.defaults.BanIpCommand;
import org.bukkit.craftbukkit.command.defaults.DeopCommand;
import org.bukkit.craftbukkit.command.defaults.GiveCommand;
import org.bukkit.craftbukkit.command.defaults.HelpCommand;
import org.bukkit.craftbukkit.command.defaults.KickCommand;
import org.bukkit.craftbukkit.command.defaults.KillCommand;
import org.bukkit.craftbukkit.command.defaults.ListCommand;
import org.bukkit.craftbukkit.command.defaults.MeCommand;
import org.bukkit.craftbukkit.command.defaults.OpCommand;
import org.bukkit.craftbukkit.command.defaults.PardonCommand;
import org.bukkit.craftbukkit.command.defaults.PardonIpCommand;
import org.bukkit.craftbukkit.command.defaults.PluginsCommand;
import org.bukkit.craftbukkit.command.defaults.ReloadCommand;
import org.bukkit.craftbukkit.command.defaults.SaveCommand;
import org.bukkit.craftbukkit.command.defaults.SaveOffCommand;
import org.bukkit.craftbukkit.command.defaults.SaveOnCommand;
import org.bukkit.craftbukkit.command.defaults.SayCommand;
import org.bukkit.craftbukkit.command.defaults.StopCommand;
import org.bukkit.craftbukkit.command.defaults.TeleportCommand;
import org.bukkit.craftbukkit.command.defaults.TellCommand;
import org.bukkit.craftbukkit.command.defaults.TimeCommand;
import org.bukkit.craftbukkit.command.defaults.VersionCommand;
import org.bukkit.craftbukkit.command.defaults.WhitelistCommand;

import java.util.Locale;

public class InternalCommandMap extends SimpleCommandMap {

    public InternalCommandMap(Server server) {
        super(server);
        setFallbackCommand(new ListCommand());
        setFallbackCommand(new StopCommand());
        setFallbackCommand(new SaveCommand());
        setFallbackCommand(new SaveOnCommand());
        setFallbackCommand(new SaveOffCommand());
        setFallbackCommand(new OpCommand());
        setFallbackCommand(new DeopCommand());
        setFallbackCommand(new BanIpCommand());
        setFallbackCommand(new PardonIpCommand());
        setFallbackCommand(new BanCommand());
        setFallbackCommand(new PardonCommand());
        setFallbackCommand(new KickCommand());
        setFallbackCommand(new TeleportCommand());
        setFallbackCommand(new GiveCommand());
        setFallbackCommand(new TimeCommand());
        setFallbackCommand(new SayCommand());
        setFallbackCommand(new WhitelistCommand());
        setFallbackCommand(new TellCommand());
        setFallbackCommand(new MeCommand());
        setFallbackCommand(new KillCommand());
        Command helpCommand = new HelpCommand();
        setFallbackCommand(helpCommand);
        setFallbackCommand("?", helpCommand);
    }

    @Override
    public void setDefaultCommands(Server server) {
        register("bukkit", new VersionCommand("version"));
        register("bukkit", new ReloadCommand("reload"));
        register("bukkit", new PluginsCommand("plugins"));
        register("poseidon", new PoseidonCommand());
        register("poseidon", new SparkCommand(server));
    }

    private void setFallbackCommand(Command command) {
        setFallbackCommand(command.getName(), command);
    }

    private void setFallbackCommand(String label, Command command) {
        this.fallbackCommands.put(label.toLowerCase(Locale.ROOT), command);
    }
}
