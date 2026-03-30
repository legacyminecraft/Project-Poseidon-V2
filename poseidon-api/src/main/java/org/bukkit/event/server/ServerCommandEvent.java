package org.bukkit.event.server;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.HandlerList;

/**
 * Server Command events
 */
public class ServerCommandEvent extends ServerEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private String command;
    private CommandSender sender;

    public ServerCommandEvent(ConsoleCommandSender console, String message) {
        super(Type.SERVER_COMMAND);
        command = message;
        sender = console;
    }

    /**
     * Gets the command that the user is attempting to execute from the console
     *
     * @return Command the user is attempting to execute
     */
    public String getCommand() {
        return command;
    }

    /**
     * Sets the command that the server will execute
     *
     * @param message New message that the server will execute
     */
    public void setCommand(String message) {
        this.command = message;
    }

    /**
     * Get the command sender.
     */
    public CommandSender getSender() {
        return sender;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
