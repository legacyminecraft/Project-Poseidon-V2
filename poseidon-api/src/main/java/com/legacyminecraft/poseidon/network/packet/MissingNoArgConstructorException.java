package com.legacyminecraft.poseidon.network.packet;

/**
 * Thrown when attempting to register an {@link InboundPacket} whose class does
 * not define an accessible no-argument constructor.
 */
public class MissingNoArgConstructorException extends RuntimeException {

    public MissingNoArgConstructorException(Class<? extends Packet> packetClass, Throwable cause) {
        super("class %s implementing InboundPacket must define an accessible no-argument constructor"
                .formatted(packetClass.getName()), cause);
    }
}
