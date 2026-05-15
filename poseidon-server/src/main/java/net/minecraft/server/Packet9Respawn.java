package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.DuplexPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketCodec;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet9Respawn extends Packet implements DuplexPacket { // Poseidon - implements DuplexPacket

    // Poseidon start
    public static final PacketCodec<Packet9Respawn> CODEC = PacketCodec.of(
            Packet9Respawn::a, Packet9Respawn::new
    );
    // Poseidon end

    public byte a;

    public Packet9Respawn() {}

    public Packet9Respawn(byte b0) {
        this.a = b0;
    }

    // Poseidon start
    public Packet9Respawn(DataInput input) throws IOException {
        this();
        a(input);
    }
    // Poseidon end

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public void a(DataInput datainputstream) throws IOException {
        this.a = datainputstream.readByte();
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeByte(this.a);
    }

    public int a() {
        return 1;
    }
}
