package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.codec.PacketEncoder;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet31RelEntityMove extends Packet30Entity {

    public static final PacketEncoder<Packet31RelEntityMove> ENCODER = Packet31RelEntityMove::a; // Poseidon

    public Packet31RelEntityMove() {}

    public Packet31RelEntityMove(int i, byte b0, byte b1, byte b2) {
        super(i);
        this.b = b0;
        this.c = b1;
        this.d = b2;
    }

    public void a(DataInput datainputstream) throws IOException {
        super.a(datainputstream);
        this.b = datainputstream.readByte();
        this.c = datainputstream.readByte();
        this.d = datainputstream.readByte();
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        super.a(dataoutputstream);
        dataoutputstream.writeByte(this.b);
        dataoutputstream.writeByte(this.c);
        dataoutputstream.writeByte(this.d);
    }

    public int a() {
        return 7;
    }
}
