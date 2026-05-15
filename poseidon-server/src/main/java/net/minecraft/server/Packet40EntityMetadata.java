package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketEncoder;
import org.jspecify.annotations.Nullable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

public class Packet40EntityMetadata extends Packet implements OutboundPacket { // Poseidon - implements OutboundPacket

    public static final PacketEncoder<Packet40EntityMetadata> ENCODER = Packet40EntityMetadata::a; // Poseidon

    public int a;
    private @Nullable List<WatchableObject> b;

    public Packet40EntityMetadata() {}

    public Packet40EntityMetadata(int i, DataWatcher datawatcher) {
        this.a = i;
        this.b = datawatcher.b();
    }

    public void a(DataInput datainputstream) throws IOException {
        this.a = datainputstream.readInt();
        this.b = DataWatcher.a(datainputstream);
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeInt(this.a);
        DataWatcher.a(this.b, dataoutputstream);
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        return 5;
    }
}
