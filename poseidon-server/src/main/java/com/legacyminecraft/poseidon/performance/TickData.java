package com.legacyminecraft.poseidon.performance;

import java.util.ArrayDeque;

public final class TickData {

    private final long interval;
    private final ArrayDeque<TickTime> tickTimes = new ArrayDeque<>();

    public TickData(long interval) {
        this.interval = interval;
    }

    public void add(TickTime tickTime) {
        TickTime oldest;
        while ((oldest = this.tickTimes.peekFirst()) != null) {
            if ((tickTime.tickStart() - oldest.tickEnd()) <= this.interval) {
                break;
            }
            this.tickTimes.pollFirst();
        }
        this.tickTimes.add(tickTime);
    }

    public double getTpsAverage(long nanosPerTick) {
        if (this.tickTimes.isEmpty()) {
            return 0.0;
        }

        long totalTicks = this.tickTimes.size();
        long totalTime = 0L;

        TickTime previous = null;
        for (TickTime tickTime : this.tickTimes) {
            if (previous != null) {
                totalTime += tickTime.tickStart() - previous.tickStart();
            } else {
                totalTime += Math.max(nanosPerTick, tickTime.tickLength());
            }
            previous = tickTime;
        }

        return (double) totalTicks / ((double) totalTime / 1_000_000_000L);
    }

    public double getMsptAverage() {
        if (this.tickTimes.isEmpty()) {
            return 0.0;
        }

        long totalTime = 0;
        long totalTicks = 0;
        for (TickTime tickTime : this.tickTimes) {
            totalTime += tickTime.tickLength();
            totalTicks++;
        }

        return (double) totalTime / totalTicks / 1_000_000L;
    }
}
