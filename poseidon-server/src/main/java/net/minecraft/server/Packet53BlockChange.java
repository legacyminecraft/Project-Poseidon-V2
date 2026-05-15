package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketEncoder;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet53BlockChange extends Packet implements OutboundPacket { // Poseidon - implements OutboundPacket

    public static final PacketEncoder<Packet53BlockChange> ENCODER = Packet53BlockChange::a; // Poseidon

    public int a;
    public int b;
    public int c;
    public int material;
    public int data;

    public Packet53BlockChange() {
        this.k = true;
    }

    public Packet53BlockChange(int i, int j, int k, World world) {
        this.k = true;
        this.a = i;
        this.b = j;
        this.c = k;
        this.material = world.getTypeId(i, j, k);
        this.data = world.getData(i, j, k);
    }

    public void a(DataInput datainputstream) throws IOException {
        this.a = datainputstream.readInt();
        this.b = datainputstream.readUnsignedByte();
        this.c = datainputstream.readInt();
        this.material = datainputstream.readUnsignedByte();
        this.data = datainputstream.readUnsignedByte();
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeInt(this.a);
        dataoutputstream.write(this.b);
        dataoutputstream.writeInt(this.c);
        dataoutputstream.write(this.material);
        dataoutputstream.write(this.data);
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        return 11;
    }
}
