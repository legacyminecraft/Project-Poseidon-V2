package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketEncoder;
import org.jspecify.annotations.Nullable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet5EntityEquipment extends Packet implements OutboundPacket { // Poseidon - implements OutboundPacket

    public static final PacketEncoder<Packet5EntityEquipment> ENCODER = Packet5EntityEquipment::a; // Poseidon

    public int a;
    public int b;
    public int c;
    public int d;

    public Packet5EntityEquipment() {}

    public Packet5EntityEquipment(int i, int j, @Nullable ItemStack itemstack) {
        this.a = i;
        this.b = j;
        if (itemstack == null) {
            this.c = -1;
            this.d = 0;
        } else {
            this.c = itemstack.id;
            this.d = itemstack.getData();
        }
    }

    public void a(DataInput datainputstream) throws IOException {
        this.a = datainputstream.readInt();
        this.b = datainputstream.readShort();
        this.c = datainputstream.readShort();
        this.d = datainputstream.readShort();
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeInt(this.a);
        dataoutputstream.writeShort(this.b);
        dataoutputstream.writeShort(this.c);
        dataoutputstream.writeShort(this.d);
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        return 8;
    }
}
