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
public @interface Positive {

    final class Factory implements Constraint.Factory<Positive, Number> {
        @Override
        public Constraint<Number> make(Positive data, Type type) {
            return value -> {
                if (value != null && value.doubleValue() <= 0) {
                    throw new SerializationException(value + " must be positive");
                }
            };
        }
    }
}
