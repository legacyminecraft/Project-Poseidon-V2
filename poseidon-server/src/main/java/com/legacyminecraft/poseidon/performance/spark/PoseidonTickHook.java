package com.legacyminecraft.poseidon.performance.spark;

import me.lucko.spark.common.tick.AbstractTickHook;

public final class PoseidonTickHook extends AbstractTickHook {

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
    public void onTick() {
        if (this.open) {
            super.onTick();
        }
    }
}
