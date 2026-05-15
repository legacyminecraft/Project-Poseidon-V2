package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketEncoder;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet200Statistic extends Packet implements OutboundPacket { // Poseidon - implements OutboundPacket

    public static final PacketEncoder<Packet200Statistic> ENCODER = Packet200Statistic::a; // Poseidon

    public int a;
    public int b;

    public Packet200Statistic() {}

    public Packet200Statistic(int i, int j) {
        this.a = i;
        this.b = j;
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public void a(DataInput datainputstream) throws IOException {
        this.a = datainputstream.readInt();
        this.b = datainputstream.readByte();
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeInt(this.a);
        dataoutputstream.writeByte(this.b);
    }

    public int a() {
        return 6;
    }
}
