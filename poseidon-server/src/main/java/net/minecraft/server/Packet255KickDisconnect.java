package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.DuplexPacket;
import com.legacyminecraft.poseidon.network.protocol.ProtocolUtil;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketCodec;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet255KickDisconnect extends Packet implements DuplexPacket { // Poseidon - implements DuplexPacket

    // Poseidon start
    public static final PacketCodec<Packet255KickDisconnect> CODEC = PacketCodec.of(
            Packet255KickDisconnect::a, Packet255KickDisconnect::new
    );
    // Poseidon end

    public String a;

    public Packet255KickDisconnect() {}

    public Packet255KickDisconnect(String s) {
        this.a = s;
    }

    // Poseidon start
    public Packet255KickDisconnect(DataInput input) throws IOException {
        this();
        a(input);
    }
    // Poseidon end

    public void a(DataInput datainputstream) throws IOException {
        this.a = ProtocolUtil.readString(datainputstream, 100); // Poseidon
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        ProtocolUtil.writeString(this.a, dataoutputstream); // Poseidon
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        return this.a.length();
    }
}
