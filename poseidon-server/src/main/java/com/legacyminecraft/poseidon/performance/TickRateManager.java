package com.legacyminecraft.poseidon.performance;

public final class TickRateManager {

    private static final long NANOS_PER_TICK = 50_000_000L;

    private long nextTickTime;
    private long currentTickStart;

    public void initialize() {
        this.nextTickTime = System.nanoTime();
    }

    public long getTimeBehind(long time) {
        return time - this.nextTickTime;
    }

    public long getNextTickTime() {
        return this.nextTickTime;
    }

    public void setNextTickTime(long time) {
        this.nextTickTime = time;
    }

    public long getCurrentTickStart() {
        return this.currentTickStart;
    }

    public void startTick() {
        this.currentTickStart = this.nextTickTime;
        this.nextTickTime += nanosPerTick();
    }

    public long nanosPerTick() {
        return NANOS_PER_TICK;
    }
}
