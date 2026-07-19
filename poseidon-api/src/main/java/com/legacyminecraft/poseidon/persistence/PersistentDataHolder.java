package com.legacyminecraft.poseidon.persistence;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents an object which can store persistent data.
 */
public interface PersistentDataHolder {

    /**
     * Returns the persistent data container which holds this object's
     * stored persistent data.
     *
     * @return the persistent data container
     */
    PersistentDataContainer getPersistentDataContainer();

    /**
     * Accesses this object's persistent data container without returning a
     * value.
     *
     * @param consumer the action to perform with the persistent data container
     */
    default void accessPersistentDataContainer(Consumer<PersistentDataContainer> consumer) {
        consumer.accept(getPersistentDataContainer());
    }

    /**
     * Accesses this object's persistent data container and returns a value.
     *
     * @param function the action to perform with the persistent data container
     * @return the value returned by the function
     * @param <T> the type of the returned value
     */
    default <T> T accessPersistentDataContainerReturning(Function<PersistentDataContainer, T> function) {
        return function.apply(getPersistentDataContainer());
    }
}
