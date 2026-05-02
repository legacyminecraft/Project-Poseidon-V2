package com.legacyminecraft.poseidon.network.connection;

/**
 * Represents a listener which is executed once a {@link ConnectionFuture}
 * completes.
 */
@FunctionalInterface
public interface ConnectionFutureListener {

    /**
     * Executes this listener with a completed future.
     *
     * @param future the completed future
     */
    void whenComplete(ConnectionFuture future);
}
