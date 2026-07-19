package com.legacyminecraft.poseidon.persistence;

import java.util.List;

/**
 * Represents a data type which converts between lists of primitive values and
 * lists of complex values. This is used by persistent data containers to store
 * and retrieve list values.
 *
 * @param <P> the primitive element type
 * @param <C> the complex element type
 */
public interface ListPersistentDataType<P, C> extends PersistentDataType<List<P>, List<C>> {

    ListPersistentDataType<Byte, Byte> BYTE = listTypeFrom(PersistentDataType.BYTE);
    ListPersistentDataType<Short, Short> SHORT = listTypeFrom(PersistentDataType.SHORT);
    ListPersistentDataType<Integer, Integer> INTEGER = listTypeFrom(PersistentDataType.INTEGER);
    ListPersistentDataType<Long, Long> LONG = listTypeFrom(PersistentDataType.LONG);
    ListPersistentDataType<Float, Float> FLOAT = listTypeFrom(PersistentDataType.FLOAT);
    ListPersistentDataType<Double, Double> DOUBLE = listTypeFrom(PersistentDataType.DOUBLE);
    ListPersistentDataType<Byte, Boolean> BOOLEAN = listTypeFrom(PersistentDataType.BOOLEAN);
    ListPersistentDataType<String, String> STRING = listTypeFrom(PersistentDataType.STRING);
    ListPersistentDataType<byte[], byte[]> BYTE_ARRAY = listTypeFrom(PersistentDataType.BYTE_ARRAY);
    ListPersistentDataType<PersistentDataContainer, PersistentDataContainer> CONTAINER = listTypeFrom(PersistentDataType.CONTAINER);

    /**
     * Returns the element data type of this list data type.
     *
     * @return the element data type
     */
    PersistentDataType<P, C> getElementType();

    /**
     * Creates a list persistent data type from an element data type.
     *
     * @param elementType the element data type
     * @return a new list data type
     */
    static <P, C> ListPersistentDataType<P, C> listTypeFrom(PersistentDataType<P, C> elementType) {
        return new ListPersistentDataTypeImpl<>(elementType);
    }
}
