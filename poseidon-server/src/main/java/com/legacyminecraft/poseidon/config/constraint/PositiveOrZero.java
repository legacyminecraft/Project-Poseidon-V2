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
public @interface PositiveOrZero {

    final class Factory implements Constraint.Factory<PositiveOrZero, Number> {
        @Override
        public Constraint<Number> make(PositiveOrZero data, Type type) {
            return value -> {
                if (value != null && value.doubleValue() < 0) {
                    throw new SerializationException(value + " must be positive or zero");
                }
            };
        }
    }
}
