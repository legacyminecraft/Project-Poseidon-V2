package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketEncoder;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet54PlayNoteBlock extends Packet implements OutboundPacket { // Poseidon - implements OutboundPacket

    public static final PacketEncoder<Packet54PlayNoteBlock> ENCODER = Packet54PlayNoteBlock::a; // Poseidon

    public int a;
    public int b;
    public int c;
    public int d;
    public int e;

    public Packet54PlayNoteBlock() {}

    public Packet54PlayNoteBlock(int i, int j, int k, int l, int i1) {
        this.a = i;
        this.b = j;
        this.c = k;
        this.d = l;
        this.e = i1;
    }

    public void a(DataInput datainputstream) throws IOException {
        this.a = datainputstream.readInt();
        this.b = datainputstream.readShort();
        this.c = datainputstream.readInt();
        this.d = datainputstream.readUnsignedByte();
        this.e = datainputstream.readUnsignedByte();
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeInt(this.a);
        dataoutputstream.writeShort(this.b);
        dataoutputstream.writeInt(this.c);
        dataoutputstream.write(this.d);
        dataoutputstream.write(this.e);
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        return 12;
    }
}
