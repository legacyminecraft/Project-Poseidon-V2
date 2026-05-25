package com.legacyminecraft.poseidon.network.proxy;

import com.legacyminecraft.poseidon.network.protocol.DuplexPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketCodec;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public record ProxyMessage(String tag, byte[] data) implements DuplexPacket {

    public static final PacketCodec<ProxyMessage> CODEC = PacketCodec.of(
            ProxyMessage::write, ProxyMessage::new
    );

    private ProxyMessage(DataInput input) throws IOException {
        byte[] tagBytes = new byte[input.readUnsignedShort()];
        input.readFully(tagBytes);
        String tag = new String(tagBytes, StandardCharsets.UTF_8);
        byte[] data = new byte[input.readUnsignedShort()];
        input.readFully(data);
        this(tag, data);
    }

    private void write(DataOutput output) throws IOException {
        byte[] tagBytes = tag().getBytes(StandardCharsets.UTF_8);
        output.writeShort(tagBytes.length);
        output.write(tagBytes);
        output.writeShort(data().length);
        output.write(data());
    }
}
