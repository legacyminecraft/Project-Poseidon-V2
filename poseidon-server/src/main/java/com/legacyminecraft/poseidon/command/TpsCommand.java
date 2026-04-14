package com.legacyminecraft.poseidon.command;

import com.legacyminecraft.poseidon.Poseidon;
import com.legacyminecraft.poseidon.performance.TickData;
import com.legacyminecraft.poseidon.performance.TickRateManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.permissions.DefaultPermissions;

public final class TpsCommand extends Command {

    private static final String TPS_PERMISSION = "poseidon.command.tps";

    public TpsCommand() {
        super("tps");
        setDescription("Displays the server's TPS for various intervals.");
        setUsage("/tps");
        setPermission(TPS_PERMISSION);
        DefaultPermissions.registerPermission(TPS_PERMISSION, "Allows access to /tps", PermissionDefault.OP);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        sender.sendMessage(ChatColor.AQUA + "Server TPS (ticks per second):");

        TickRateManager tickRateManager = Poseidon.getTickRateManager();
        String intervals = formatTps(tickRateManager.getTickData1s()) + " (1s), " +
                formatTps(tickRateManager.getTickData5s()) + " (5s), " +
                formatTps(tickRateManager.getTickData10s()) + " (10s), " +
                formatTps(tickRateManager.getTickData15s()) + " (15s), " +
                formatTps(tickRateManager.getTickData1m()) + " (1m), " +
                formatTps(tickRateManager.getTickData5m()) + " (5m), " +
                formatTps(tickRateManager.getTickData15m()) + " (15m)";

        sender.sendMessage(intervals);
        return true;
    }

    private String formatTps(TickData tickData) {
        double tps = tickData.getTpsAverage(Poseidon.getTickRateManager().nanosPerTick());
        ChatColor color = tps >= 19.0 ? ChatColor.GREEN : tps >= 16.0 ? ChatColor.YELLOW : ChatColor.RED;
        return color + String.format("%.2f", tps);
    }
}
