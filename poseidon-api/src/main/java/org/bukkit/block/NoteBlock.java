package org.bukkit.block;

import com.legacyminecraft.poseidon.block.TileState;
import org.bukkit.Instrument;
import org.bukkit.Note;

/**
 * Represents a note.
 */
public interface NoteBlock extends TileState { // Poseidon - extends TileState

    /**
     * Gets the note.
     *
     * @return
     */
    Note getNote();

    /**
     * Gets the note.
     *
     * @return
     */
    byte getRawNote();

    /**
     * Set the note.
     *
     * @param note
     */
    void setNote(Note note);

    /**
     * Set the note.
     *
     * @param note
     */
    void setRawNote(byte note);

    /**
     * Attempts to play the note at block<br />
     * <br />
     * If the block is no longer a note block, this will return false
     *
     * @return true if successful, otherwise false
     */
    boolean play();

    /**
     * Plays an arbitrary note with an arbitrary instrument
     *
     * @return true if successful, otherwise false
     */
    boolean play(byte instrument, byte note);

    /**
     * Plays an arbitrary note with an arbitrary instrument
     *
     * @return true if successful, otherwise false
     */
    boolean play(Instrument instrument, Note note);
}
