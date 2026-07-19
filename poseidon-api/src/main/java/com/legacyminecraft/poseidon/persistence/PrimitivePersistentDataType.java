package com.legacyminecraft.poseidon.persistence;

record PrimitivePersistentDataType<T>(PrimitiveType<T> primitiveType) implements PersistentDataType<T, T> {

    @Override
    public PrimitiveType<T> getPrimitiveType() {
        return this.primitiveType;
    }

    @Override
    public Class<T> getComplexType() {
        return this.primitiveType.typeClass();
    }

    @Override
    public T toPrimitive(T complex) {
        return complex;
    }

    @Override
    public T fromPrimitive(T primitive) {
        return primitive;
    }
}
