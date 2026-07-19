package com.legacyminecraft.poseidon.persistence;

import net.minecraft.server.NBTBase;

import java.util.function.Function;
import java.util.function.Predicate;

public record TagAdapter<P, T extends NBTBase>(
        Function<P, T> serializer,
        Function<T, P> deserializer,
        Predicate<NBTBase> matcher) {

    public T serialize(P primitive) {
        return this.serializer.apply(primitive);
    }

    public P deserialize(T tag) {
        return this.deserializer.apply(tag);
    }

    public boolean matches(NBTBase tag) {
        return this.matcher.test(tag);
    }
}
