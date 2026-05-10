package com.legacyminecraft.poseidon.network.connection;

/**
 * Represents a future which completes when a connection operation finishes.
 */
public interface ConnectionFuture {

    /**
     * Returns the connection associated with this future.
     *
     * @return the connection
     */
    PlayerConnection getConnection();

    /**
     * Returns if this future is completed.
     *
     * @return {@code true} if this future is completed
     */
    boolean isCompleted();

    /**
     * Adds a listener to this future. The listener will be executed once this
     * future completes.
     *
     * @param listener the listener
     * @return this future
     */
    ConnectionFuture addListener(ConnectionFutureListener listener);

    /**
     * Adds an array of listeners to this future. The listeners will be
     * executed once this future completes.
     *
     * @param listeners the array of listeners
     * @return this future
     */
    ConnectionFuture addListeners(ConnectionFutureListener... listeners);

    /**
     * Removes a listener from this future. The removed listener will not be
     * executed once this future completes.
     *
     * @param listener the listener
     * @return this future
     */
    ConnectionFuture removeListener(ConnectionFutureListener listener);

    /**
     * Removes an array of listeners from this future. The removed listeners
     * will not be executed once this future completes.
     *
     * @param listeners the array of listeners
     * @return this future
     */
    ConnectionFuture removeListeners(ConnectionFutureListener... listeners);
}
