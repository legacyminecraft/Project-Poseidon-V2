package com.legacyminecraft.poseidon.persistence;

record PrimitiveTypeImpl<T>(Class<T> typeClass) implements PrimitiveType<T> {
}
