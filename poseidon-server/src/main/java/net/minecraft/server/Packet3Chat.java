package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.DuplexPacket;
import com.legacyminecraft.poseidon.network.protocol.ProtocolUtil;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketCodec;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet3Chat extends Packet implements DuplexPacket { // Poseidon - implements DuplexPacket

    // Poseidon start
    public static final PacketCodec<Packet3Chat> CODEC = PacketCodec.of(
            Packet3Chat::a, Packet3Chat::new
    );
    // Poseidon end

    public String message;

    public Packet3Chat() {}

    public Packet3Chat(String s) {
        /* CraftBukkit start - handle this later
        if (s.length() > 119) {
            s = s.substring(0, 119);
        }
        // CraftBukkit end */

        this.message = s;
    }

    // Poseidon start
    public Packet3Chat(DataInput input) throws IOException {
        this();
        a(input);
    }
    // Poseidon end

    public void a(DataInput datainputstream) throws IOException { // CraftBukkit
        this.message = ProtocolUtil.readString(datainputstream, 119); // Poseidon
    }

    public void a(DataOutput dataoutputstream) throws IOException { // CraftBukkit
        ProtocolUtil.writeString(this.message, dataoutputstream); // Poseidon
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        return this.message.length();
    }
}
