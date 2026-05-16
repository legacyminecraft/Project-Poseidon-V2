package com.legacyminecraft.poseidon.network.connection;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class ConnectionFutureImpl implements ConnectionFuture {

    private static final Logger log = LoggerFactory.getLogger(ConnectionFutureImpl.class);

    private final PlayerConnection connection;
    private @Nullable List<ConnectionFutureListener> waitingListeners;
    private volatile boolean completed = false;

    public ConnectionFutureImpl(PlayerConnection connection) {
        Preconditions.checkArgument(connection != null, "connection cannot be null");

        this.connection = connection;
    }

    @Override
    public PlayerConnection getConnection() {
        return this.connection;
    }

    @Override
    public boolean isCompleted() {
        return this.completed;
    }

    public void complete() {
        synchronized (this) {
            if (!this.completed) {
                this.completed = true;
                if (waitingListeners != null) {
                    this.waitingListeners.forEach(this::invokeListener);
                }
            }
        }
    }

    @Override
    public ConnectionFuture addListener(ConnectionFutureListener listener) {
        Preconditions.checkArgument(listener != null, "listener cannot be null");

        synchronized (this) {
            if (this.completed) {
                invokeListener(listener);
            } else {
                if (this.waitingListeners == null) {
                    this.waitingListeners = new ObjectArrayList<>();
                }
                this.waitingListeners.add(listener);
            }
            return this;
        }
    }

    @Override
    public ConnectionFuture addListeners(ConnectionFutureListener... listeners) {
        Preconditions.checkArgument(listeners != null, "listeners cannot be null");

        synchronized (this) {
            for (int i = 0; i < listeners.length; i++) {
                addListener(listeners[i]);
            }
            return this;
        }
    }

    @Override
    public ConnectionFuture removeListener(ConnectionFutureListener listener) {
        Preconditions.checkArgument(listener != null, "listener cannot be null");

        synchronized (this) {
            if (this.waitingListeners != null) {
                this.waitingListeners.remove(listener);
            }
            return this;
        }
    }

    @Override
    public ConnectionFuture removeListeners(ConnectionFutureListener... listeners) {
        Preconditions.checkArgument(listeners != null, "listeners cannot be null");

        synchronized (this) {
            if (this.waitingListeners != null) {
                for (int i = 0; i < listeners.length; i++) {
                    removeListener(listeners[i]);
                }
            }
            return this;
        }
    }

    private void invokeListener(ConnectionFutureListener listener) {
        try {
            listener.whenComplete(this);
        } catch (Throwable t) {
            log.warn("An error occurred while invoking listener {}", listener.getClass().getName(), t);
        }
    }
}
