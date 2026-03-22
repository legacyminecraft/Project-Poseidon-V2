package net.minecraft.server;

import org.jspecify.annotations.Nullable;

import java.util.Objects;

class PlayerListEntry {

    final long a;
    Object b;
    @Nullable PlayerListEntry c;
    final int d;

    PlayerListEntry(int i, long j, Object object, @Nullable PlayerListEntry playerlistentry) {
        this.b = object;
        this.c = playerlistentry;
        this.a = j;
        this.d = i;
    }

    public final long a() {
        return this.a;
    }

    public final Object b() {
        return this.b;
    }

    public final boolean equals(Object object) {
        if (object instanceof PlayerListEntry playerlistentry) {
            long olong = this.a();
            long olong1 = playerlistentry.a();

            if (olong == olong1) {
                Object object1 = this.b();
                Object object2 = playerlistentry.b();

                return Objects.equals(object1, object2);
            }
        }

        return false;
    }

    public final int hashCode() {
        return PlayerList.d(this.a);
    }

    public final String toString() {
        return this.a() + "=" + this.b();
    }
}
