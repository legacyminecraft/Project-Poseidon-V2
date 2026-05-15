package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketEncoder;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet61 extends Packet implements OutboundPacket { // Poseidon - implements OutboundPacket

    public static final PacketEncoder<Packet61> ENCODER = Packet61::a; // Poseidon

    public int a;
    public int b;
    public int c;
    public int d;
    public int e;

    public Packet61() {}

    public Packet61(int i, int j, int k, int l, int i1) {
        this.a = i;
        this.c = j;
        this.d = k;
        this.e = l;
        this.b = i1;
    }

    public void a(DataInput datainputstream) throws IOException {
        this.a = datainputstream.readInt();
        this.c = datainputstream.readInt();
        this.d = datainputstream.readByte();
        this.e = datainputstream.readInt();
        this.b = datainputstream.readInt();
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeInt(this.a);
        dataoutputstream.writeInt(this.c);
        dataoutputstream.writeByte(this.d);
        dataoutputstream.writeInt(this.e);
        dataoutputstream.writeInt(this.b);
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        return 20;
    }
}
