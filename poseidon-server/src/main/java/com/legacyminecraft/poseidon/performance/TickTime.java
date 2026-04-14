package com.legacyminecraft.poseidon.performance;

public record TickTime(long tickStart, long tickEnd) {

    public long tickLength() {
        return tickEnd() - tickStart();
    }
}
