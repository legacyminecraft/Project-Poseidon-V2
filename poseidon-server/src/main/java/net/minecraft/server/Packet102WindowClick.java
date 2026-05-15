package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.InboundPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketDecoder;
import org.jspecify.annotations.Nullable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet102WindowClick extends Packet implements InboundPacket { // Poseidon - implements InboundPacket

    public static final PacketDecoder<Packet102WindowClick> DECODER = Packet102WindowClick::new; // Poseidon

    public int a;
    public int b;
    public int c;
    public short d;
    public @Nullable ItemStack e;
    public boolean f;

    public Packet102WindowClick() {}

    // Poseidon start
    public Packet102WindowClick(DataInput input) throws IOException {
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
        this.c = datainputstream.readByte();
        this.d = datainputstream.readShort();
        this.f = datainputstream.readBoolean();
        short short1 = datainputstream.readShort();

        if (short1 >= 0) {
            byte b0 = datainputstream.readByte();
            short short2 = datainputstream.readShort();

            this.e = new ItemStack(short1, b0, short2);
        } else {
            this.e = null;
        }
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeByte(this.a);
        dataoutputstream.writeShort(this.b);
        dataoutputstream.writeByte(this.c);
        dataoutputstream.writeShort(this.d);
        dataoutputstream.writeBoolean(this.f);
        if (this.e == null) {
            dataoutputstream.writeShort(-1);
        } else {
            dataoutputstream.writeShort(this.e.id);
            dataoutputstream.writeByte(this.e.count);
            dataoutputstream.writeShort(this.e.getData());
        }
    }

    public int a() {
        return 11;
    }
}
