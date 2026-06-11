package org.bukkit.util;

import com.google.common.base.Preconditions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * Provides utility methods for line-wrapping text which should be displayed to
 * clients.
 */
public class TextWrapper {

    private static final int[] characterWidths = new int[] {
        1, 9, 9, 8, 8, 8, 8, 7, 9, 8, 9, 9, 8, 9, 9, 9,
        8, 8, 8, 8, 9, 9, 8, 9, 8, 8, 8, 8, 8, 9, 9, 9,
        4, 2, 5, 6, 6, 6, 6, 3, 5, 5, 5, 6, 2, 6, 2, 6,
        6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 2, 2, 5, 6, 5, 6,
        7, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 6, 6, 6, 6, 6,
        6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 4, 6, 6,
        3, 6, 6, 6, 6, 6, 5, 6, 6, 2, 6, 5, 3, 6, 6, 6,
        6, 6, 6, 6, 4, 6, 6, 6, 6, 6, 6, 5, 2, 5, 7, 6,
        6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 3, 6, 6,
        6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6,
        6, 3, 6, 6, 6, 6, 6, 6, 6, 7, 6, 6, 6, 2, 6, 6,
        8, 9, 9, 6, 6, 6, 8, 8, 6, 8, 8, 8, 8, 8, 6, 6,
        9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,
        9, 9, 9, 9, 9, 9, 9, 9, 9, 6, 9, 9, 9, 5, 9, 9,
        8, 7, 7, 8, 7, 8, 8, 8, 7, 8, 8, 7, 9, 9, 6, 7,
        7, 7, 7, 7, 9, 6, 7, 8, 7, 6, 6, 9, 7, 6, 7, 1
    };

    // Poseidon start - private -> public
    /**
     * The character which precedes a color code.
     */
    public static final char COLOR_CHAR = '\u00A7';

    /**
     * The width of a vanilla client's chat window in pixels.
     */
    public static final int CHAT_WINDOW_WIDTH = 320;

    /**
     * The maximum length strings sent to clients may be.
     */
    public static final int CHAT_STRING_LENGTH = 119; // Poseidon - rename

    /**
     * A string containing all characters which may be displayed in chat.
     */
    public static final String allowedChars;
    // Poseidon end

    /**
     * Performs line-wrapping on a text, so that each line can fit inside the
     * width of a client's chat window, with respect to
     * {@link #CHAT_WINDOW_WIDTH} and {@link #CHAT_STRING_LENGTH}.
     * <p>
     * The line-wrapped text will correctly preserve chat colors.
     *
     * @param text the input text
     * @return the line-wrapped text as an array of lines
     */
    public static String[] wrapText(final String text) {
        Preconditions.checkArgument(text != null, "text cannot be null"); // Poseidon

        final StringBuilder out = new StringBuilder();
        char colorChar = 'f';
        int lineWidth = 0;
        int lineLength = 0;

        // Go over the message char by char.
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            // Get the color
            if (ch == COLOR_CHAR && i < text.length() - 1) {
                // We might need a linebreak ... so ugly ;(
                if (lineLength + 2 > CHAT_STRING_LENGTH) {
                    out.append('\n');
                    lineLength = 0;
                    if (colorChar != 'f' && colorChar != 'F') {
                        out.append(COLOR_CHAR).append(colorChar);
                        lineLength += 2;
                    }
                }
                colorChar = text.charAt(++i);
                out.append(COLOR_CHAR).append(colorChar);
                lineLength += 2;
                continue;
            }

            // Figure out if it's allowed
            int index = allowedChars.indexOf(ch);
            if (index == -1) {
                // Invalid character .. skip it.
                continue;
            } else {
                // Sadly needed as the allowedChars string misses the first
                index += 32;
            }

            // Find the width
            final int width = characterWidths[index];

            // See if we need a linebreak
            if (lineLength + 1 > CHAT_STRING_LENGTH || lineWidth + width >= CHAT_WINDOW_WIDTH) {
                out.append('\n');
                lineLength = 0;

                // Re-apply the last color if it isn't the default
                if (colorChar != 'f' && colorChar != 'F') {
                    out.append(COLOR_CHAR).append(colorChar);
                    lineLength += 2;
                }
                lineWidth = width;
            } else {
                lineWidth += width;
            }
            out.append(ch);
            lineLength++;
        }

        // Return it split
        return out.toString().split("\n");
    }

    // Poseidon start
    /**
     * Calculates the width of a text in pixels based on the default
     * character widths.
     *
     * @param text the input text
     * @return the width of the text in pixels
     */
    public static int widthInPixels(final String text) {
        Preconditions.checkArgument(text != null, "string cannot be null");

        int length = 0;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            if (ch == COLOR_CHAR && i < text.length() - 1) {
                i++;
                continue;
            }

            int index = allowedChars.indexOf(ch);
            if (index == -1) {
                continue;
            }

            index += 32;
            length += characterWidths[index];
        }

        return length;
    }

    static {
        InputStream in = TextWrapper.class.getResourceAsStream("/font.txt");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            allowedChars = reader.lines()
                    .filter(line -> !line.startsWith("#"))
                    .collect(Collectors.joining());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read font.txt", e);
        }
    }
    // Poseidon end
}
