package com.legacyminecraft.poseidon.command;

import com.legacyminecraft.poseidon.performance.spark.PoseidonSpark;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;

public final class SparkCommand extends Command {

    private final PoseidonSpark spark;

    public SparkCommand(Server server) {
        super("spark");
        setDescription("Spark commands.");
        setUsage("/spark");
        this.spark = ((CraftServer) server).spark;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        this.spark.executeCommand(sender, args);
        return true;
    }
}
