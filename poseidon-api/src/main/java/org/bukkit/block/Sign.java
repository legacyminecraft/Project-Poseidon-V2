package org.bukkit.block;

import com.legacyminecraft.poseidon.block.TileState;

/**
 * Represents either a SignPost or a WallSign
 */
public interface Sign extends TileState { // Poseidon - extends TileState

    /**
     * Gets all the lines of text currently on this sign.
     *
     * @return Array of Strings containing each line of text
     */
    String[] getLines();

    /**
     * Gets the line of text at the specified index.
     *
     * For example, getLine(0) will return the first line of text.
     *
     * @param index Line number to get the text from, starting at 0
     * @throws IndexOutOfBoundsException Thrown when the line does not exist
     * @return Text on the given line
     */
    String getLine(int index) throws IndexOutOfBoundsException;

    /**
     * Sets the line of text at the specified index.
     *
     * For example, setLine(0, "Line One") will set the first line of text to
     * "Line One".
     *
     * @param index Line number to set the text at, starting from 0
     * @param line New text to set at the specified index
     * @throws IndexOutOfBoundsException
     */
    void setLine(int index, String line) throws IndexOutOfBoundsException;
}
