package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketEncoder;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet4UpdateTime extends Packet implements OutboundPacket { // Poseidon - implements OutboundPacket

    public static final PacketEncoder<Packet4UpdateTime> ENCODER = Packet4UpdateTime::a; // Poseidon

    public long a;

    public Packet4UpdateTime() {}

    public Packet4UpdateTime(long i) {
        this.a = i;
    }

    public void a(DataInput datainputstream) throws IOException {
        this.a = datainputstream.readLong();
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeLong(this.a);
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        return 8;
    }
}
