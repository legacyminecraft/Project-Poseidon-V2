package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketEncoder;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet8UpdateHealth extends Packet implements OutboundPacket { // Poseidon - implements OutboundPacket

    public static final PacketEncoder<Packet8UpdateHealth> ENCODER = Packet8UpdateHealth::a; // Poseidon

    public int a;

    public Packet8UpdateHealth() {}

    public Packet8UpdateHealth(int i) {
        this.a = i;
    }

    public void a(DataInput datainputstream) throws IOException {
        this.a = datainputstream.readShort();
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeShort(this.a);
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        return 2;
    }
}
