package com.legacyminecraft.poseidon.performance.spark;

import me.lucko.spark.common.tick.AbstractTickReporter;

public final class PoseidonTickReporter extends AbstractTickReporter {

    private boolean open = false;

    @Override
    public void start() {
        this.open = true;
    }

    @Override
    public void close() {
        this.open = false;
    }

    @Override
    protected void onTick(double duration) {
        if (this.open) {
            super.onTick(duration);
        }
    }
}
