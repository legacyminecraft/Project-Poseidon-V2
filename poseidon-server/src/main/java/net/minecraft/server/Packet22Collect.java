package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketEncoder;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet22Collect extends Packet implements OutboundPacket { // Poseidon - implements OutboundPacket

    public static final PacketEncoder<Packet22Collect> ENCODER = Packet22Collect::a; // Poseidon

    public int a;
    public int b;

    public Packet22Collect() {}

    public Packet22Collect(int i, int j) {
        this.a = i;
        this.b = j;
    }

    public void a(DataInput datainputstream) throws IOException {
        this.a = datainputstream.readInt();
        this.b = datainputstream.readInt();
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeInt(this.a);
        dataoutputstream.writeInt(this.b);
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        return 8;
    }
}
