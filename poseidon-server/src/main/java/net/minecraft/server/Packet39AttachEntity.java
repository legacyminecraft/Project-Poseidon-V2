package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketEncoder;
import org.jspecify.annotations.Nullable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet39AttachEntity extends Packet implements OutboundPacket { // Poseidon - implements OutboundPacket

    public static final PacketEncoder<Packet39AttachEntity> ENCODER = Packet39AttachEntity::a; // Poseidon

    public int a;
    public int b;

    public Packet39AttachEntity() {}

    public Packet39AttachEntity(Entity entity, @Nullable Entity entity1) {
        this.a = entity.id;
        this.b = entity1 != null ? entity1.id : -1;
    }

    public int a() {
        return 8;
    }

    public void a(DataInput datainputstream) throws IOException {
        this.a = datainputstream.readInt();
        this.b = datainputstream.readInt();
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeInt(this.a);
        dataoutputstream.writeInt(this.b);
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }
}
