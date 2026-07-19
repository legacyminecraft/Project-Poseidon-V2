package com.legacyminecraft.poseidon.persistence;

/**
 * Represents a data type which converts between primitive values and complex
 * values. This is used by persistent data containers to store and retrieve
 * values.
 *
 * @param <P> the primitive type
 * @param <C> the complex type
 */
public interface PersistentDataType<P, C> {

    PersistentDataType<Byte, Byte> BYTE = new PrimitivePersistentDataType<>(PrimitiveType.BYTE);
    PersistentDataType<Short, Short> SHORT = new PrimitivePersistentDataType<>(PrimitiveType.SHORT);
    PersistentDataType<Integer, Integer> INTEGER = new PrimitivePersistentDataType<>(PrimitiveType.INTEGER);
    PersistentDataType<Long, Long> LONG = new PrimitivePersistentDataType<>(PrimitiveType.LONG);
    PersistentDataType<Float, Float> FLOAT = new PrimitivePersistentDataType<>(PrimitiveType.FLOAT);
    PersistentDataType<Double, Double> DOUBLE = new PrimitivePersistentDataType<>(PrimitiveType.DOUBLE);
    PersistentDataType<Byte, Boolean> BOOLEAN = new BooleanPersistentDataType();
    PersistentDataType<String, String> STRING = new PrimitivePersistentDataType<>(PrimitiveType.STRING);
    PersistentDataType<byte[], byte[]> BYTE_ARRAY = new PrimitivePersistentDataType<>(PrimitiveType.BYTE_ARRAY);
    PersistentDataType<PersistentDataContainer, PersistentDataContainer> CONTAINER = new PrimitivePersistentDataType<>(PrimitiveType.CONTAINER);

    /**
     * Returns the primitive type of this data type.
     *
     * @return the primitive type
     */
    PrimitiveType<P> getPrimitiveType();

    /**
     * Returns the complex type of this data type.
     *
     * @return the complex type
     */
    Class<C> getComplexType();

    /**
     * Converts a complex value to a primitive value.
     *
     * @param complex the complex value
     * @return the primitive value
     */
    P toPrimitive(C complex);

    /**
     * Converts a primitive value to a complex value.
     *
     * @param primitive the primitive value
     * @return the complex value
     */
    C fromPrimitive(P primitive);
}
