package com.legacyminecraft.poseidon.persistence;

import org.jspecify.annotations.Nullable;

import java.util.Set;

/**
 * Represents a container which stores persistent data using key-value entries.
 * <p>
 * In addition to being able to store simple values, it can also store lists
 * and other containers, which allows for more complex data structures to be
 * created.
 * <p>
 * A persistent data container internally stores all its values as primitive
 * values and converts them to complex values when accessed. Persistent data
 * types are used to convert between primitive and complex values.
 */
public interface PersistentDataContainer {

    /**
     * Stores a new entry in this container, or replaces the value if an entry
     * with the specified key already exists.
     *
     * @param key the key to store the value under
     * @param type the type of the value
     * @param value the value to store
     */
    <P, C> void set(String key, PersistentDataType<P, C> type, C value);

    /**
     * Removes an entry from this container.
     *
     * @param key the key of the entry to remove
     */
    void remove(String key);

    /**
     * Returns if this container has an entry with the specified key and a
     * value that is of the specified type.
     *
     * @param key the key of the entry
     * @param type the type of the value
     * @return {@code true} if an entry with the key and type exists
     */
    <P, C> boolean has(String key, PersistentDataType<P, C> type);

    /**
     * Returns if this container has an entry with the specified key.
     *
     * @param key the key of the entry
     * @return {@code true} if an entry with the key exists
     */
    boolean has(String key);

    /**
     * Returns the value which is stored under the specified key.
     *
     * @param key the key the value is stored under
     * @param type the type of the value
     * @return the value, or {@code null} if no entry with the key and type
     *         exists
     */
    <P, C> @Nullable C get(String key, PersistentDataType<P, C> type);

    /**
     * Returns the value which is stored under the specified key, or returns a
     * default value if the value does not exist.
     *
     * @param key the key the value is stored under
     * @param type the type of the value
     * @return the value, or the default value if no entry with the key and
     *         type exists
     */
    <P, C> C getOrDefault(String key, PersistentDataType<P, C> type, C defaultValue);

    /**
     * Returns a copy of the keys of all entries in this container.
     *
     * @return the keys of all entries in this container
     */
    Set<String> getKeys();

    /**
     * Returns if this container is empty, meaning there are no entries in it.
     *
     * @return {@code true} if this container is empty
     */
    boolean isEmpty();

    /**
     * Copies all entries in this container to another container.
     *
     * @param other the container to copy to
     * @param replace {@code true} if entries in this container should replace
     *        entries in the other container if the keys match
     */
    void copyTo(PersistentDataContainer other, boolean replace);
}
