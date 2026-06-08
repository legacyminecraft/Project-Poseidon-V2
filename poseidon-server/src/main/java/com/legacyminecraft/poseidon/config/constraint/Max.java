package com.legacyminecraft.poseidon.config.constraint;

import org.spongepowered.configurate.objectmapping.meta.Constraint;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Type;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Max {
    int value();

    final class Factory implements Constraint.Factory<Max, Number> {
        @Override
        public Constraint<Number> make(Max data, Type type) {
            return value -> {
                if (value != null && value.intValue() > data.value()) {
                    throw new SerializationException(value + " must not be greater than the maximum " + data.value());
                }
            };
        }
    }
}
