package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.DuplexPacket;
import com.legacyminecraft.poseidon.network.protocol.ProtocolUtil;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketCodec;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet1Login extends Packet implements DuplexPacket { // Poseidon - implements DuplexPacket

    // Poseidon start
    public static final PacketCodec<Packet1Login> CODEC = PacketCodec.of(
            Packet1Login::a, Packet1Login::new
    );
    // Poseidon end

    public int a;
    public String name;
    public long c;
    public byte d;

    public Packet1Login() {}

    public Packet1Login(String s, int i, long j, byte b0) {
        this.name = s;
        this.a = i;
        this.c = j;
        this.d = b0;
    }

    // Poseidon start
    public Packet1Login(DataInput input) throws IOException {
        this();
        a(input);
    }
    // Poseidon end

    public void a(DataInput datainputstream) throws IOException {
        this.a = datainputstream.readInt();
        this.name = ProtocolUtil.readString(datainputstream, 16); // Poseidon
        this.c = datainputstream.readLong();
        this.d = datainputstream.readByte();
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeInt(this.a);
        ProtocolUtil.writeString(this.name, dataoutputstream); // Poseidon
        dataoutputstream.writeLong(this.c);
        dataoutputstream.writeByte(this.d);
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        return 4 + this.name.length() + 4 + 5;
    }
}
