package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketEncoder;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet28EntityVelocity extends Packet implements OutboundPacket { // Poseidon - implements OutboundPacket

    public static final PacketEncoder<Packet28EntityVelocity> ENCODER = Packet28EntityVelocity::a; // Poseidon

    public int a;
    public int b;
    public int c;
    public int d;

    public Packet28EntityVelocity() {}

    public Packet28EntityVelocity(Entity entity) {
        this(entity.id, entity.motX, entity.motY, entity.motZ);
    }

    public Packet28EntityVelocity(int i, double d0, double d1, double d2) {
        this.a = i;
        double d3 = 3.9D;

        if (d0 < -d3) {
            d0 = -d3;
        }

        if (d1 < -d3) {
            d1 = -d3;
        }

        if (d2 < -d3) {
            d2 = -d3;
        }

        if (d0 > d3) {
            d0 = d3;
        }

        if (d1 > d3) {
            d1 = d3;
        }

        if (d2 > d3) {
            d2 = d3;
        }

        this.b = (int) (d0 * 8000.0D);
        this.c = (int) (d1 * 8000.0D);
        this.d = (int) (d2 * 8000.0D);
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
        return 10;
    }
}
