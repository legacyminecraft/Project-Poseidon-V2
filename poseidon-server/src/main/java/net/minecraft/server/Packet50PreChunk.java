package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketEncoder;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet50PreChunk extends Packet implements OutboundPacket { // Poseidon - implements OutboundPacket

    public static final PacketEncoder<Packet50PreChunk> ENCODER = Packet50PreChunk::a; // Poseidon

    public int a;
    public int b;
    public boolean c;

    public Packet50PreChunk() {
        this.k = false;
    }

    public Packet50PreChunk(int i, int j, boolean flag) {
        this.k = false;
        this.a = i;
        this.b = j;
        this.c = flag;
    }

    public void a(DataInput datainputstream) throws IOException {
        this.a = datainputstream.readInt();
        this.b = datainputstream.readInt();
        this.c = datainputstream.readUnsignedByte() != 0;
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeInt(this.a);
        dataoutputstream.writeInt(this.b);
        dataoutputstream.write(this.c ? 1 : 0);
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        return 9;
    }
}
