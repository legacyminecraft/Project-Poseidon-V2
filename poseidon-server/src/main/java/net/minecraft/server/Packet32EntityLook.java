package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.codec.PacketEncoder;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet32EntityLook extends Packet30Entity {

    public static final PacketEncoder<Packet32EntityLook> ENCODER = Packet32EntityLook::a; // Poseidon

    public Packet32EntityLook() {
        this.g = true;
    }

    public Packet32EntityLook(int i, byte b0, byte b1) {
        super(i);
        this.e = b0;
        this.f = b1;
        this.g = true;
    }

    public void a(DataInput datainputstream) throws IOException {
        super.a(datainputstream);
        this.e = datainputstream.readByte();
        this.f = datainputstream.readByte();
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        super.a(dataoutputstream);
        dataoutputstream.writeByte(this.e);
        dataoutputstream.writeByte(this.f);
    }

    public int a() {
        return 6;
    }
}
