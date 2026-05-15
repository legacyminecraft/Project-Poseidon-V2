package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.DuplexPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketCodec;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet18ArmAnimation extends Packet implements DuplexPacket { // Poseidon - implements DuplexPacket

    // Poseidon start
    public static final PacketCodec<Packet18ArmAnimation> CODEC = PacketCodec.of(
            Packet18ArmAnimation::a, Packet18ArmAnimation::new
    );
    // Poseidon end

    public int a;
    public int b;

    public Packet18ArmAnimation() {}

    public Packet18ArmAnimation(Entity entity, int i) {
        this.a = entity.id;
        this.b = i;
    }

    // Poseidon start
    public Packet18ArmAnimation(DataInput input) throws IOException {
        this();
        a(input);
    }
    // Poseidon end

    public void a(DataInput datainputstream) throws IOException {
        this.a = datainputstream.readInt();
        this.b = datainputstream.readByte();
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeInt(this.a);
        dataoutputstream.writeByte(this.b);
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        return 5;
    }
}
