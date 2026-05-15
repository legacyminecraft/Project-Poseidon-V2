package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketEncoder;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet17 extends Packet implements OutboundPacket { // Poseidon - implements OutboundPacket

    public static final PacketEncoder<Packet17> ENCODER = Packet17::a; // Poseidon

    public int a;
    public int b;
    public int c;
    public int d;
    public int e;

    public Packet17() {}

    public Packet17(Entity entity, int i, int j, int k, int l) {
        this.e = i;
        this.b = j;
        this.c = k;
        this.d = l;
        this.a = entity.id;
    }

    public void a(DataInput datainputstream) throws IOException {
        this.a = datainputstream.readInt();
        this.e = datainputstream.readByte();
        this.b = datainputstream.readInt();
        this.c = datainputstream.readByte();
        this.d = datainputstream.readInt();
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeInt(this.a);
        dataoutputstream.writeByte(this.e);
        dataoutputstream.writeInt(this.b);
        dataoutputstream.writeByte(this.c);
        dataoutputstream.writeInt(this.d);
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        return 14;
    }
}
