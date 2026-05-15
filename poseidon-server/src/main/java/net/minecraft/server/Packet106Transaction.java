package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.DuplexPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketCodec;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet106Transaction extends Packet implements DuplexPacket { // Poseidon - implements DuplexPacket

    // Poseidon start
    public static final PacketCodec<Packet106Transaction> CODEC = PacketCodec.of(
            Packet106Transaction::a, Packet106Transaction::new
    );
    // Poseidon end

    public int a;
    public short b;
    public boolean c;

    public Packet106Transaction() {}

    public Packet106Transaction(int i, short short1, boolean flag) {
        this.a = i;
        this.b = short1;
        this.c = flag;
    }

    // Poseidon start
    public Packet106Transaction(DataInput input) throws IOException {
        this();
        a(input);
    }
    // Poseidon end

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public void a(DataInput datainputstream) throws IOException {
        this.a = datainputstream.readByte();
        this.b = datainputstream.readShort();
        this.c = datainputstream.readByte() != 0;
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeByte(this.a);
        dataoutputstream.writeShort(this.b);
        dataoutputstream.writeByte(this.c ? 1 : 0);
    }

    public int a() {
        return 4;
    }
}
