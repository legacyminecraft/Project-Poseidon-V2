package com.legacyminecraft.poseidon.config.type;

import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.function.Predicate;

public final class InetAddressSerializer extends ScalarSerializer<InetAddress> {

    public static final InetAddressSerializer INSTANCE = new InetAddressSerializer();

    private InetAddressSerializer() {
        super(InetAddress.class);
    }

    @Override
    public InetAddress deserialize(Type type, Object obj) throws SerializationException {
        try {
            return InetAddress.ofLiteral(String.valueOf(obj));
        } catch (IllegalArgumentException e) {
            throw new SerializationException(e);
        }
    }

    @Override
    protected Object serialize(InetAddress address, Predicate<Class<?>> typeSupported) {
        return address.getHostAddress();
    }
}
