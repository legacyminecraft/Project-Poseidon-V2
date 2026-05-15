package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.codec.PacketCodec;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet12PlayerLook extends Packet10Flying {

    // Poseidon start
    public static final PacketCodec<Packet12PlayerLook> CODEC = PacketCodec.of(
            Packet12PlayerLook::a, Packet12PlayerLook::new
    );
    // Poseidon end

    public Packet12PlayerLook() {
        this.hasLook = true;
    }

    // Poseidon start
    public Packet12PlayerLook(DataInput input) throws IOException {
        this();
        a(input);
    }
    // Poseidon end

    public void a(DataInput datainputstream) throws IOException {
        this.yaw = datainputstream.readFloat();
        this.pitch = datainputstream.readFloat();
        super.a(datainputstream);
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeFloat(this.yaw);
        dataoutputstream.writeFloat(this.pitch);
        super.a(dataoutputstream);
    }

    public int a() {
        return 9;
    }
}
