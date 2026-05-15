package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.DuplexPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketCodec;

import java.io.DataInput;
import java.io.DataOutput;

public class Packet0KeepAlive extends Packet implements DuplexPacket { // Poseidon - implements DuplexPacket

    // Poseidon start
    public static final PacketCodec<Packet0KeepAlive> CODEC = PacketCodec.of(
            Packet0KeepAlive::a, Packet0KeepAlive::new
    );
    // Poseidon end

    public Packet0KeepAlive() {}

    // Poseidon start
    public Packet0KeepAlive(DataInput input) {
        this();
        a(input);
    }
    // Poseidon end

    public void a(NetHandler nethandler) {}

    public void a(DataInput datainputstream) {}

    public void a(DataOutput dataoutputstream) {}

    public int a() {
        return 0;
    }
}
