package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketEncoder;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet131 extends Packet implements OutboundPacket { // Poseidon - implements OutboundPacket

    public static final PacketEncoder<Packet131> ENCODER = Packet131::a; // Poseidon

    public short a;
    public short b;
    public byte[] c;

    public Packet131() {
        this.k = true;
    }

    public Packet131(short short1, short short2, byte[] abyte) {
        this.k = true;
        this.a = short1;
        this.b = short2;
        this.c = abyte;
    }

    public void a(DataInput datainputstream) throws IOException {
        this.a = datainputstream.readShort();
        this.b = datainputstream.readShort();
        this.c = new byte[datainputstream.readByte() & 255];
        datainputstream.readFully(this.c);
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeShort(this.a);
        dataoutputstream.writeShort(this.b);
        dataoutputstream.writeByte(this.c.length);
        dataoutputstream.write(this.c);
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        return 4 + this.c.length;
    }
}
