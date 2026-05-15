package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketEncoder;
import org.jspecify.annotations.Nullable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet70Bed extends Packet implements OutboundPacket { // Poseidon - implements OutboundPacket

    public static final PacketEncoder<Packet70Bed> ENCODER = Packet70Bed::a; // Poseidon
    public static final @Nullable String[] a = new @Nullable String[] { "tile.bed.notValid", null, null};

    public int b;

    public Packet70Bed() {}

    public Packet70Bed(int i) {
        this.b = i;
    }

    public void a(DataInput datainputstream) throws IOException {
        this.b = datainputstream.readByte();
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeByte(this.b);
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        return 1;
    }
}
