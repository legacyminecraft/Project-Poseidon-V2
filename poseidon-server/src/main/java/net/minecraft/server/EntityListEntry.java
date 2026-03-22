package net.minecraft.server;

import org.jspecify.annotations.Nullable;

import java.util.Objects;

class EntityListEntry {

    final int a;
    Object b;
    @Nullable EntityListEntry c;
    final int d;

    EntityListEntry(int i, int j, Object object, @Nullable EntityListEntry entitylistentry) {
        this.b = object;
        this.c = entitylistentry;
        this.a = j;
        this.d = i;
    }

    public final int a() {
        return this.a;
    }

    public final Object b() {
        return this.b;
    }

    public final boolean equals(Object object) {
        if (object instanceof EntityListEntry entitylistentry) {
            int integer = this.a();
            int integer1 = entitylistentry.a();

            if (integer == integer1) {
                Object object1 = this.b();
                Object object2 = entitylistentry.b();

                return Objects.equals(object1, object2);
            }
        }

        return false;
    }

    public final int hashCode() {
        return EntityList.f(this.a);
    }

    public final String toString() {
        return this.a() + "=" + this.b();
    }
}
