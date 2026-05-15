package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.DuplexPacket;
import com.legacyminecraft.poseidon.network.protocol.ProtocolUtil;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketCodec;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet130UpdateSign extends Packet implements DuplexPacket { // Poseidon - implements DuplexPacket

    // Poseidon start
    public static final PacketCodec<Packet130UpdateSign> CODEC = PacketCodec.of(
            Packet130UpdateSign::a, Packet130UpdateSign::new
    );
    // Poseidon end

    public int x;
    public int y;
    public int z;
    public String[] lines;

    public Packet130UpdateSign() {
        this.k = true;
    }

    public Packet130UpdateSign(int i, int j, int k, String[] astring) {
        this.k = true;
        this.x = i;
        this.y = j;
        this.z = k;
        this.lines = astring;
    }

    // Poseidon start
    public Packet130UpdateSign(DataInput input) throws IOException {
        this();
        a(input);
    }
    // Poseidon end

    public void a(DataInput datainputstream) throws IOException {
        this.x = datainputstream.readInt();
        this.y = datainputstream.readShort();
        this.z = datainputstream.readInt();
        this.lines = new String[4];

        for (int i = 0; i < 4; ++i) {
            this.lines[i] = ProtocolUtil.readString(datainputstream, 15); // Poseidon
        }
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeInt(this.x);
        dataoutputstream.writeShort(this.y);
        dataoutputstream.writeInt(this.z);

        for (int i = 0; i < 4; ++i) {
            ProtocolUtil.writeString(this.lines[i], dataoutputstream); // Poseidon
        }
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        int i = 0;

        for (int j = 0; j < 4; ++j) {
            i += this.lines[j].length();
        }

        return i;
    }
}
