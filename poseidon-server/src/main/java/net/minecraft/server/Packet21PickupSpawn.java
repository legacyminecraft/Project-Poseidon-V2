package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketEncoder;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet21PickupSpawn extends Packet implements OutboundPacket { // Poseidon - implements OutboundPacket

    public static final PacketEncoder<Packet21PickupSpawn> ENCODER = Packet21PickupSpawn::a; // Poseidon

    public int a;
    public int b;
    public int c;
    public int d;
    public byte e;
    public byte f;
    public byte g;
    public int h;
    public int i;
    public int l;

    public Packet21PickupSpawn() {}

    public Packet21PickupSpawn(EntityItem entityitem) {
        this.a = entityitem.id;
        this.h = entityitem.itemStack.id;
        this.i = entityitem.itemStack.count;
        this.l = entityitem.itemStack.getData();
        this.b = MathHelper.floor(entityitem.locX * 32.0D);
        this.c = MathHelper.floor(entityitem.locY * 32.0D);
        this.d = MathHelper.floor(entityitem.locZ * 32.0D);
        this.e = (byte) ((int) (entityitem.motX * 128.0D));
        this.f = (byte) ((int) (entityitem.motY * 128.0D));
        this.g = (byte) ((int) (entityitem.motZ * 128.0D));
    }

    public void a(DataInput datainputstream) throws IOException {
        this.a = datainputstream.readInt();
        this.h = datainputstream.readShort();
        this.i = datainputstream.readByte();
        this.l = datainputstream.readShort();
        this.b = datainputstream.readInt();
        this.c = datainputstream.readInt();
        this.d = datainputstream.readInt();
        this.e = datainputstream.readByte();
        this.f = datainputstream.readByte();
        this.g = datainputstream.readByte();
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeInt(this.a);
        dataoutputstream.writeShort(this.h);
        dataoutputstream.writeByte(this.i);
        dataoutputstream.writeShort(this.l);
        dataoutputstream.writeInt(this.b);
        dataoutputstream.writeInt(this.c);
        dataoutputstream.writeInt(this.d);
        dataoutputstream.writeByte(this.e);
        dataoutputstream.writeByte(this.f);
        dataoutputstream.writeByte(this.g);
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        return 24;
    }
}
