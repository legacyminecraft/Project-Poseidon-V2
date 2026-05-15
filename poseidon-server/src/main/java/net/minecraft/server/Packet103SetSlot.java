package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketEncoder;
import org.jspecify.annotations.Nullable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet103SetSlot extends Packet implements OutboundPacket { // Poseidon - implements OutboundPacket

    public static final PacketEncoder<Packet103SetSlot> ENCODER = Packet103SetSlot::a; // Poseidon

    public int a;
    public int b;
    public @Nullable ItemStack c;

    public Packet103SetSlot() {}

    public Packet103SetSlot(int i, int j, @Nullable ItemStack itemstack) {
        this.a = i;
        this.b = j;
        this.c = itemstack == null ? itemstack : itemstack.cloneItemStack();
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public void a(DataInput datainputstream) throws IOException {
        this.a = datainputstream.readByte();
        this.b = datainputstream.readShort();
        short short1 = datainputstream.readShort();

        if (short1 >= 0) {
            byte b0 = datainputstream.readByte();
            short short2 = datainputstream.readShort();

            this.c = new ItemStack(short1, b0, short2);
        } else {
            this.c = null;
        }
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeByte(this.a);
        dataoutputstream.writeShort(this.b);
        if (this.c == null) {
            dataoutputstream.writeShort(-1);
        } else {
            dataoutputstream.writeShort(this.c.id);
            dataoutputstream.writeByte(this.c.count);
            dataoutputstream.writeShort(this.c.getData());
        }
    }

    public int a() {
        return 8;
    }
}
