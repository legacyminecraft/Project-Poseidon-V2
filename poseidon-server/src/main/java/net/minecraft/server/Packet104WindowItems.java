package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketEncoder;
import org.jspecify.annotations.Nullable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

public class Packet104WindowItems extends Packet implements OutboundPacket { // Poseidon - implements OutboundPacket

    public static final PacketEncoder<Packet104WindowItems> ENCODER = Packet104WindowItems::a; // Poseidon

    public int a;
    public @Nullable ItemStack[] b;

    public Packet104WindowItems() {}

    public Packet104WindowItems(int i, List<@Nullable ItemStack> list) {
        this.a = i;
        this.b = new ItemStack[list.size()];

        for (int j = 0; j < this.b.length; ++j) {
            ItemStack itemstack = list.get(j);

            this.b[j] = itemstack == null ? null : itemstack.cloneItemStack();
        }
    }

    public void a(DataInput datainputstream) throws IOException {
        this.a = datainputstream.readByte();
        short short1 = datainputstream.readShort();

        this.b = new ItemStack[short1];

        for (int i = 0; i < short1; ++i) {
            short short2 = datainputstream.readShort();

            if (short2 >= 0) {
                byte b0 = datainputstream.readByte();
                short short3 = datainputstream.readShort();

                this.b[i] = new ItemStack(short2, b0, short3);
            }
        }
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeByte(this.a);
        dataoutputstream.writeShort(this.b.length);

        for (int i = 0; i < this.b.length; ++i) {
            if (this.b[i] == null) {
                dataoutputstream.writeShort(-1);
            } else {
                dataoutputstream.writeShort((short) this.b[i].id);
                dataoutputstream.writeByte((byte) this.b[i].count);
                dataoutputstream.writeShort((short) this.b[i].getData());
            }
        }
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        return 3 + this.b.length * 5;
    }
}
