package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.codec.PacketCodec;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet11PlayerPosition extends Packet10Flying {

    // Poseidon start
    public static final PacketCodec<Packet11PlayerPosition> CODEC = PacketCodec.of(
            Packet11PlayerPosition::a, Packet11PlayerPosition::new
    );
    // Poseidon end

    public Packet11PlayerPosition() {
        this.h = true;
    }

    // Poseidon start
    public Packet11PlayerPosition(DataInput input) throws IOException {
        this();
        a(input);
    }
    // Poseidon end

    public void a(DataInput datainputstream) throws IOException {
        this.x = datainputstream.readDouble();
        this.y = datainputstream.readDouble();
        this.stance = datainputstream.readDouble();
        this.z = datainputstream.readDouble();
        super.a(datainputstream);
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeDouble(this.x);
        dataoutputstream.writeDouble(this.y);
        dataoutputstream.writeDouble(this.stance);
        dataoutputstream.writeDouble(this.z);
        super.a(dataoutputstream);
    }

    public int a() {
        return 33;
    }
}
