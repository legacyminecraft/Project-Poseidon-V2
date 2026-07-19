package com.legacyminecraft.poseidon.persistence;

import java.util.List;

/**
 * Represents a primitive type of a persistent data type.
 *
 * @param <T> the primitive type
 */
public sealed interface PrimitiveType<T> permits PrimitiveTypeImpl {

    PrimitiveType<Byte> BYTE = new PrimitiveTypeImpl<>(Byte.class);
    PrimitiveType<Short> SHORT = new PrimitiveTypeImpl<>(Short.class);
    PrimitiveType<Integer> INTEGER = new PrimitiveTypeImpl<>(Integer.class);
    PrimitiveType<Long> LONG = new PrimitiveTypeImpl<>(Long.class);
    PrimitiveType<Float> FLOAT = new PrimitiveTypeImpl<>(Float.class);
    PrimitiveType<Double> DOUBLE = new PrimitiveTypeImpl<>(Double.class);
    PrimitiveType<String> STRING = new PrimitiveTypeImpl<>(String.class);
    PrimitiveType<byte[]> BYTE_ARRAY = new PrimitiveTypeImpl<>(byte[].class);
    @SuppressWarnings("rawtypes")
    PrimitiveType<List> LIST = new PrimitiveTypeImpl<>(List.class);
    PrimitiveType<PersistentDataContainer> CONTAINER = new PrimitiveTypeImpl<>(PersistentDataContainer.class);

    /**
     * Returns the class of this primitive type.
     *
     * @return the type class
     */
    Class<T> typeClass();
}
