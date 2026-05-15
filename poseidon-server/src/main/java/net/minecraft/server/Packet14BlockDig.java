package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.InboundPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketDecoder;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet14BlockDig extends Packet implements InboundPacket { // Poseidon - implements InboundPacket

    public static final PacketDecoder<Packet14BlockDig> DECODER = Packet14BlockDig::new; // Poseidon

    public int a;
    public int b;
    public int c;
    public int face;
    public int e;

    public Packet14BlockDig() {}

    // Poseidon start
    public Packet14BlockDig(DataInput input) throws IOException {
        this();
        a(input);
    }
    // Poseidon end

    public void a(DataInput datainputstream) throws IOException {
        this.e = datainputstream.readUnsignedByte();
        this.a = datainputstream.readInt();
        this.b = datainputstream.readUnsignedByte();
        this.c = datainputstream.readInt();
        this.face = datainputstream.readUnsignedByte();
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.write(this.e);
        dataoutputstream.writeInt(this.a);
        dataoutputstream.write(this.b);
        dataoutputstream.writeInt(this.c);
        dataoutputstream.write(this.face);
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        return 11;
    }
}
