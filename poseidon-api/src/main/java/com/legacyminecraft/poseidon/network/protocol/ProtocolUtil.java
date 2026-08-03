package com.legacyminecraft.poseidon.network.protocol;

import com.google.common.base.Preconditions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Provides various utility methods for interacting with the network protocol.
 */
public final class ProtocolUtil {

    /**
     * The maximum amount of characters a string sent over the network is
     * allowed to have.
     */
    public static final int MAX_STRING_LENGTH = Short.MAX_VALUE;

    /**
     * The maximum length of a VarInt in bytes.
     */
    public static final int MAX_VARINT_LENGTH = 5;

    private ProtocolUtil() {
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
     * Reads a VarInt from a data input.
     *
     * @param input the data input
     * @return the read VarInt
     * @throws IOException if the received VarInt is longer than
     *         {@link #MAX_VARINT_LENGTH} bytes, or if an I/O error occurs
     */
    public static int readVarInt(DataInput input) throws IOException {
        Preconditions.checkArgument(input != null, "input cannot be null");

        int value = 0;
        int i = 0;
        int b;
        while (((b = input.readUnsignedByte()) & 0x80) != 0) {
            value |= (b & 0x7F) << i;
            i += 7;
            if (i > 7 * MAX_VARINT_LENGTH) {
                throw new IOException("received VarInt is longer than maximum " + MAX_VARINT_LENGTH + " bytes");
            }
        }
        return value | (b << i);
    }

    /**
     * Writes a VarInt to a data output.
     *
     * @param value the VarInt
     * @param output the data output
     * @throws IOException if an I/O error occurs
     */
    public static void writeVarInt(int value, DataOutput output) throws IOException {
        Preconditions.checkArgument(output != null, "output cannot be null");

        while ((value & 0xFFFFFF80) != 0L) {
            output.write((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        output.write(value & 0x7F);
    }

    /**
     * Reads a VarInt length-prefixed UTF-8 string from a data input.
     *
     * @param input the data input
     * @return the string
     * @throws IOException if an I/O error occurs
     */
    public static String readUtf8String(DataInput input) throws IOException {
        Preconditions.checkArgument(input != null, "input cannot be null");

        int length = readVarInt(input);
        byte[] bytes = new byte[length];
        input.readFully(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Writes a VarInt length-prefixed UTF-8 string to a data output.
     *
     * @param string the string
     * @param output the data output
     * @throws IllegalArgumentException if the string length exceeds
     *         {@link #MAX_STRING_LENGTH}
     * @throws IOException if an I/O error occurs
     */
    public static void writeUtf8String(String string, DataOutput output) throws IOException {
        Preconditions.checkArgument(string != null, "string cannot be null");
        Preconditions.checkArgument(output != null, "output cannot be null");
        Preconditions.checkArgument(string.length() <= MAX_STRING_LENGTH,
                "string cannot be longer than " + MAX_STRING_LENGTH + " characters");

        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        writeVarInt(bytes.length, output);
        output.write(bytes);
    }
}
