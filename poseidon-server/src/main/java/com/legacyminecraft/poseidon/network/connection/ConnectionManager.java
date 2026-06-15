package com.legacyminecraft.poseidon.network.connection;

public interface ConnectionManager<T extends AbstractPlayerConnection> {

    Iterable<T> getConnections();

    void tickConnections();
}
