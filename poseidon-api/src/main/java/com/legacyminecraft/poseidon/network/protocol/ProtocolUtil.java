package com.legacyminecraft.poseidon.network.protocol;

import com.google.common.base.Preconditions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Provides various utility methods for interacting with the network protocol.
 */
public final class ProtocolUtil {

    /**
     * The maximum amount of characters a string sent over the network is
     * allowed to have.
     */
    public static final int MAX_STRING_LENGTH = Short.MAX_VALUE;

    private ProtocolUtil() {
    }

    /**
     * Writes a string to a data output.
     *
     * @param string the string
     * @param output the data output
     * @throws IllegalArgumentException if the string length exceeds
     *         {@link #MAX_STRING_LENGTH}
     * @throws IOException if an I/O error occurs
     */
    public static void writeString(String string, DataOutput output) throws IOException {
        Preconditions.checkArgument(string != null, "string cannot be null");
        Preconditions.checkArgument(output != null, "output cannot be null");
        Preconditions.checkArgument(string.length() <= MAX_STRING_LENGTH,
                "string cannot be longer than " + MAX_STRING_LENGTH + " characters");

        output.writeShort(string.length());
        output.writeChars(string);
    }

    /**
     * Reads a string from a data input.
     *
     * @param input the data input
     * @param maxLength the maximum length the string is allowed to have
     * @return the read string
     * @throws IOException if the received string length is negative or exceeds
     *         {@link #MAX_STRING_LENGTH}, or if an I/O error occurs
     */
    public static String readString(DataInput input, int maxLength) throws IOException {
        Preconditions.checkArgument(input != null, "input cannot be null");
        Preconditions.checkArgument(maxLength >= 0 && maxLength <= MAX_STRING_LENGTH,
                "maxLength must be between 0 and " + MAX_STRING_LENGTH);

        short length = input.readShort();
        if (length < 0) {
            throw new IOException("received string length is negative: " + length);
        } else if (length > maxLength) {
            throw new IOException("received string length exceeds maximum length: " + length + " > " + maxLength);
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                sb.append(input.readChar());
            }
            return sb.toString();
        }
    }
}
