package com.legacyminecraft.poseidon.command;

import com.legacyminecraft.poseidon.Poseidon;
import com.legacyminecraft.poseidon.performance.TickData;
import com.legacyminecraft.poseidon.performance.TickRateManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.permissions.DefaultPermissions;

public final class MsptCommand extends Command {

    private static final String MSPT_PERMISSION = "poseidon.command.mspt";

    public MsptCommand() {
        super("mspt");
        setDescription("Displays the server's MSPT for various intervals.");
        setUsage("/mspt");
        setPermission(MSPT_PERMISSION);
        DefaultPermissions.registerPermission(MSPT_PERMISSION, "Allows access to /mspt", PermissionDefault.OP);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        sender.sendMessage(ChatColor.AQUA + "Server MSPT (milliseconds per tick):");

        TickRateManager tickRateManager = Poseidon.getTickRateManager();
        String intervals = formatMspt(tickRateManager.getTickData1s()) + " (1s), " +
                formatMspt(tickRateManager.getTickData5s()) + " (5s), " +
                formatMspt(tickRateManager.getTickData10s()) + " (10s), " +
                formatMspt(tickRateManager.getTickData15s()) + " (15s), " +
                formatMspt(tickRateManager.getTickData1m()) + " (1m), " +
                formatMspt(tickRateManager.getTickData5m()) + " (5m), " +
                formatMspt(tickRateManager.getTickData15m()) + " (15m)";

        sender.sendMessage(intervals);
        return true;
    }

    private String formatMspt(TickData tickData) {
        double mspt = tickData.getMsptAverage();
        ChatColor color = mspt < 40.0 ? ChatColor.GREEN : mspt < 50.0 ? ChatColor.YELLOW : ChatColor.RED;
        return color + String.format("%.2f", mspt);
    }
}
