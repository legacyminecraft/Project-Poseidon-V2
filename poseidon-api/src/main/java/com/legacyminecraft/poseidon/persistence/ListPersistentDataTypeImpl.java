package com.legacyminecraft.poseidon.persistence;

import com.google.common.collect.Lists;

import java.util.List;

record ListPersistentDataTypeImpl<P, C>(PersistentDataType<P, C> elementType) implements ListPersistentDataType<P, C> {

    @Override
    @SuppressWarnings("unchecked")
    public PrimitiveType<List<P>> getPrimitiveType() {
        return (PrimitiveType<List<P>>) (Object) PrimitiveType.LIST;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<List<C>> getComplexType() {
        return (Class<List<C>>) (Object) List.class;
    }

    @Override
    public List<P> toPrimitive(List<C> complex) {
        return complex.stream().map(this.elementType::toPrimitive).toList();
    }

    @Override
    public List<C> fromPrimitive(List<P> primitive) {
        return Lists.transform(primitive, this.elementType::fromPrimitive);
    }

    @Override
    public PersistentDataType<P, C> getElementType() {
        return this.elementType;
    }
}
