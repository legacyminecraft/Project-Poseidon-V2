package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketEncoder;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet6SpawnPosition extends Packet implements OutboundPacket { // Poseidon - implements OutboundPacket

    public static final PacketEncoder<Packet6SpawnPosition> ENCODER = Packet6SpawnPosition::a; // Poseidon

    public int x;
    public int y;
    public int z;

    public Packet6SpawnPosition() {}

    public Packet6SpawnPosition(int i, int j, int k) {
        this.x = i;
        this.y = j;
        this.z = k;
    }

    public void a(DataInput datainputstream) throws IOException {
        this.x = datainputstream.readInt();
        this.y = datainputstream.readInt();
        this.z = datainputstream.readInt();
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeInt(this.x);
        dataoutputstream.writeInt(this.y);
        dataoutputstream.writeInt(this.z);
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        return 12;
    }
}
