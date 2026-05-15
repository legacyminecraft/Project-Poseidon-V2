package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.DuplexPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketCodec;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet10Flying extends Packet implements DuplexPacket { // Poseidon - implements DuplexPacket

    // Poseidon start
    public static final PacketCodec<Packet10Flying> CODEC = PacketCodec.of(
            Packet10Flying::a, Packet10Flying::new
    );
    // Poseidon end

    public double x;
    public double y;
    public double z;
    public double stance;
    public float yaw;
    public float pitch;
    public boolean g;
    public boolean h;
    public boolean hasLook;

    public Packet10Flying() {}

    // Poseidon start
    public Packet10Flying(DataInput input) throws IOException {
        this();
        a(input);
    }
    // Poseidon end

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public void a(DataInput datainputstream) throws IOException {
        this.g = datainputstream.readUnsignedByte() != 0;
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.write(this.g ? 1 : 0);
    }

    public int a() {
        return 1;
    }
}
