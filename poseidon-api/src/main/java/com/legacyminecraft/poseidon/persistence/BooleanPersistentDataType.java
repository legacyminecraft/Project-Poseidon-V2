package com.legacyminecraft.poseidon.persistence;

final class BooleanPersistentDataType implements PersistentDataType<Byte, Boolean> {

    @Override
    public PrimitiveType<Byte> getPrimitiveType() {
        return PrimitiveType.BYTE;
    }

    @Override
    public Class<Boolean> getComplexType() {
        return Boolean.class;
    }

    @Override
    public Byte toPrimitive(Boolean complex) {
        return (byte) (complex ? 1 : 0);
    }

    @Override
    public Boolean fromPrimitive(Byte primitive) {
        return primitive != 0;
    }
}
