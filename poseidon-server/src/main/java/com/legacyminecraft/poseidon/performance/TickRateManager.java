package com.legacyminecraft.poseidon.performance;

import java.util.concurrent.TimeUnit;

public final class TickRateManager {

    private static final long NANOS_PER_TICK = 50_000_000L;

    private final TickData tickData1s = new TickData(TimeUnit.SECONDS.toNanos(1));
    private final TickData tickData5s = new TickData(TimeUnit.SECONDS.toNanos(5));
    private final TickData tickData10s = new TickData(TimeUnit.SECONDS.toNanos(10));
    private final TickData tickData15s = new TickData(TimeUnit.SECONDS.toNanos(15));
    private final TickData tickData1m = new TickData(TimeUnit.MINUTES.toNanos(1));
    private final TickData tickData5m = new TickData(TimeUnit.MINUTES.toNanos(5));
    private final TickData tickData15m = new TickData(TimeUnit.MINUTES.toNanos(15));

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

    public void startTick() {
        this.currentTickStart = this.nextTickTime;
        this.nextTickTime += nanosPerTick();
    }

    public void recordTick() {
        long tickEnd = System.nanoTime();
        TickTime tickTime = new TickTime(this.currentTickStart, tickEnd);
        this.tickData1s.add(tickTime);
        this.tickData5s.add(tickTime);
        this.tickData10s.add(tickTime);
        this.tickData15s.add(tickTime);
        this.tickData1m.add(tickTime);
        this.tickData5m.add(tickTime);
        this.tickData15m.add(tickTime);
    }

    public long nanosPerTick() {
        return NANOS_PER_TICK;
    }

    public TickData getTickData1s() {
        return this.tickData1s;
    }

    public TickData getTickData5s() {
        return this.tickData5s;
    }

    public TickData getTickData10s() {
        return this.tickData10s;
    }

    public TickData getTickData15s() {
        return this.tickData15s;
    }

    public TickData getTickData1m() {
        return this.tickData1m;
    }

    public TickData getTickData5m() {
        return this.tickData5m;
    }

    public TickData getTickData15m() {
        return this.tickData15m;
    }
}
