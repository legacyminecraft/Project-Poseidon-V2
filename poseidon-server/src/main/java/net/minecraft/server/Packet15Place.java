package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.InboundPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketDecoder;
import org.jspecify.annotations.Nullable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet15Place extends Packet implements InboundPacket { // Poseidon - implements InboundPacket

    public static final PacketDecoder<Packet15Place> DECODER = Packet15Place::new; // Poseidon

    public int a;
    public int b;
    public int c;
    public int face;
    public @Nullable ItemStack itemstack;

    public Packet15Place() {}

    // Poseidon start
    public Packet15Place(DataInput input) throws IOException {
        this();
        a(input);
    }
    // Poseidon end

    public void a(DataInput datainputstream) throws IOException {
        this.a = datainputstream.readInt();
        this.b = datainputstream.readUnsignedByte();
        this.c = datainputstream.readInt();
        this.face = datainputstream.readUnsignedByte();
        short short1 = datainputstream.readShort();

        if (short1 >= 0) {
            byte b0 = datainputstream.readByte();
            short short2 = datainputstream.readShort();

            this.itemstack = new ItemStack(short1, b0, short2);
        } else {
            this.itemstack = null;
        }
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeInt(this.a);
        dataoutputstream.write(this.b);
        dataoutputstream.writeInt(this.c);
        dataoutputstream.write(this.face);
        if (this.itemstack == null) {
            dataoutputstream.writeShort(-1);
        } else {
            dataoutputstream.writeShort(this.itemstack.id);
            dataoutputstream.writeByte(this.itemstack.count);
            dataoutputstream.writeShort(this.itemstack.getData());
        }
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        return 15;
    }
}
