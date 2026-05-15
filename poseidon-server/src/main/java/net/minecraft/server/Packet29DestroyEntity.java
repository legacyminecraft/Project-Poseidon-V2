package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketEncoder;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet29DestroyEntity extends Packet implements OutboundPacket { // Poseidon - implements OutboundPacket

    public static final PacketEncoder<Packet29DestroyEntity> ENCODER = Packet29DestroyEntity::a; // Poseidon

    public int a;

    public Packet29DestroyEntity() {}

    public Packet29DestroyEntity(int i) {
        this.a = i;
    }

    public void a(DataInput datainputstream) throws IOException {
        this.a = datainputstream.readInt();
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeInt(this.a);
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        return 4;
    }
}
