package org.bukkit.craftbukkit.command;

import net.kyori.ansi.ANSIComponentRenderer;
import net.kyori.ansi.StyleOps;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.jspecify.annotations.Nullable;

import java.util.logging.Logger;

public class ColouredConsoleSender extends ConsoleCommandSender {

    private static final Logger log = Logger.getLogger("Minecraft"); // Poseidon

    // Poseidon - remove fields

    public ColouredConsoleSender(CraftServer server) {
        super(server);
    }

    @Override
    public void sendMessage(String message) {
        log.info(chatColorsToAnsi(message)); // Poseidon
    }

    // Poseidon start - convert chat colors to ansi
    private static String chatColorsToAnsi(String text) {
        ANSIComponentRenderer.ToString<ChatColor> renderer = ANSIComponentRenderer.toString(ChatColorStyle.instance);
        ChatColor lastColor = ChatColor.WHITE;
        renderer.pushStyle(lastColor);

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '\u00A7' && i < text.length() - 1) {
                ChatColor color = null;
                try {
                    color = ChatColor.getByCode(Integer.parseInt(String.valueOf(text.charAt(i + 1)), 16));
                } catch (NumberFormatException e) {
                }

                if (color != null) {
                    renderer.popStyle(lastColor);
                    lastColor = color;
                    renderer.pushStyle(lastColor);
                    i++;
                    continue;
                }
            }

            renderer.text(String.valueOf(ch));
        }

        renderer.popStyle(lastColor);
        renderer.complete();
        return renderer.asString();
    }

    private static final class ChatColorStyle implements StyleOps<ChatColor> {
        private static final ChatColorStyle instance = new ChatColorStyle();

        @Override
        public int color(ChatColor color) {
            return switch (color) {
                case BLACK -> 0x000000;
                case DARK_BLUE -> 0x0000AA;
                case DARK_GREEN -> 0x00AA00;
                case DARK_AQUA -> 0x00AAAA;
                case DARK_RED -> 0xAA0000;
                case DARK_PURPLE -> 0xAA00AA;
                case GOLD -> 0xFFAA00;
                case GRAY -> 0xAAAAAA;
                case DARK_GRAY -> 0x555555;
                case BLUE -> 0x5555FF;
                case GREEN -> 0x55FF55;
                case AQUA -> 0x55FFFF;
                case RED -> 0xFF5555;
                case LIGHT_PURPLE -> 0xFF55FF;
                case YELLOW -> 0xFFFF55;
                default -> 0xFFFFFF;
            };
        }

        @Override
        public State bold(ChatColor color) {
            return State.UNSET;
        }

        @Override
        public State italics(ChatColor color) {
            return State.UNSET;
        }

        @Override
        public State underlined(ChatColor color) {
            return State.UNSET;
        }

        @Override
        public State strikethrough(ChatColor color) {
            return State.UNSET;
        }

        @Override
        public State obfuscated(ChatColor color) {
            return State.UNSET;
        }

        @Override
        public @Nullable String font(ChatColor color) {
            return null;
        }
    }
    // Poseidon end
}
