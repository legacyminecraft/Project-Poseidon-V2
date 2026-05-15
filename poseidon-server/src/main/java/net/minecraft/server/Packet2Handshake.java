package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.DuplexPacket;
import com.legacyminecraft.poseidon.network.protocol.ProtocolUtil;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketCodec;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet2Handshake extends Packet implements DuplexPacket { // Poseidon - implements DuplexPacket

    // Poseidon start
    public static final PacketCodec<Packet2Handshake> CODEC = PacketCodec.of(
            Packet2Handshake::a, Packet2Handshake::new
    );
    // Poseidon end

    public String a;

    public Packet2Handshake() {}

    public Packet2Handshake(String s) {
        this.a = s;
    }

    // Poseidon start
    public Packet2Handshake(DataInput input) throws IOException {
        this();
        a(input);
    }
    // Poseidon end

    public void a(DataInput datainputstream) throws IOException {
        this.a = ProtocolUtil.readString(datainputstream, 32); // Poseidon
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        ProtocolUtil.writeString(this.a, dataoutputstream); // Poseidon
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        return 4 + this.a.length() + 4;
    }
}
