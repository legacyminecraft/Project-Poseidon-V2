package org.bukkit;

import com.google.common.base.Preconditions;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * All supported color values for chat
 */
public enum ChatColor {

    /**
     * Represents black
     */
    BLACK(0x0),
    /**
     * Represents dark blue
     */
    DARK_BLUE(0x1),
    /**
     * Represents dark green
     */
    DARK_GREEN(0x2),
    /**
     * Represents dark blue (aqua)
     */
    DARK_AQUA(0x3),
    /**
     * Represents dark red
     */
    DARK_RED(0x4),
    /**
     * Represents dark purple
     */
    DARK_PURPLE(0x5),
    /**
     * Represents gold
     */
    GOLD(0x6),
    /**
     * Represents gray
     */
    GRAY(0x7),
    /**
     * Represents dark gray
     */
    DARK_GRAY(0x8),
    /**
     * Represents blue
     */
    BLUE(0x9),
    /**
     * Represents green
     */
    GREEN(0xA),
    /**
     * Represents aqua
     */
    AQUA(0xB),
    /**
     * Represents red
     */
    RED(0xC),
    /**
     * Represents light purple
     */
    LIGHT_PURPLE(0xD),
    /**
     * Represents yellow
     */
    YELLOW(0xE),
    /**
     * Represents white
     */
    WHITE(0xF);

    // Poseidon start
    /**
     * The special character which precedes all chat color codes.
     */
    public static final char COLOR_CHAR = '\u00A7';
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "[0-9A-Fa-f]");
    // Poseidon end

    private final int code;
    private static final Map<Integer, ChatColor> colors = new HashMap<>();

    ChatColor(final int code) {
        this.code = code;
    }

    /**
     * Gets the data value associated with this color
     *
     * @return An integer value of this color code
     */
    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return String.format("\u00A7%x", code);
    }

    /**
     * Gets the color represented by the specified color code
     *
     * @param code Code to check
     * @return Associative {@link ChatColor} with the given code, or null if it doesn't exist
     */
    public static @Nullable ChatColor getByCode(final int code) {
        return colors.get(code);
    }

    /**
     * Strips the given message of all color codes
     *
     * @param input String to strip of color
     * @return A copy of the input string, without any coloring
     */
    public static @Nullable String stripColor(final @Nullable String input) {
        if (input == null) {
            return null;
        }

        return STRIP_COLOR_PATTERN.matcher(input).replaceAll(""); // Poseidon
    }

    // Poseidon start - backport translateAlternateColorCodes method
    /**
     * Translates a string using an alternate color code character into a
     * string that uses the internal {@link #COLOR_CHAR} color code
     * character. The alternate color code character will only be replaced if
     * it is immediately followed by 0-9, A-F or a-f.
     *
     * @param altColorChar The alternate color code character to replace.
     *        Ex: {@literal &}
     * @param textToTranslate Text containing the alternate color code
     *        character.
     * @return Text containing the {@link #COLOR_CHAR} color code character.
     */
    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        Preconditions.checkArgument(textToTranslate != null, "textToTranslate cannot be null");

        char[] chars = textToTranslate.toCharArray();
        for (int i = 0; i < chars.length - 1; i++) {
            if (chars[i] == altColorChar && "0123456789AaBbCcDdEeFf".indexOf(chars[i + 1]) > -1) {
                chars[i] = ChatColor.COLOR_CHAR;
                chars[i + 1] = Character.toLowerCase(chars[i + 1]);
            }
        }
        return new String(chars);
    }
    // Poseidon end

    static {
        for (ChatColor color : ChatColor.values()) {
            colors.put(color.getCode(), color);
        }
    }
}
