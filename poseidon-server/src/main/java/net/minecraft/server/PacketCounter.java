package net.minecraft.server;

import org.jspecify.annotations.Nullable;

class PacketCounter {

    private int a;
    private long b;

    private PacketCounter() {}

    public void a(int i) {
        ++this.a;
        this.b += i;
    }

    PacketCounter(@Nullable EmptyClass1 emptyclass1) {
        this();
    }
}
