package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.InboundPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketDecoder;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet7UseEntity extends Packet implements InboundPacket { // Poseidon - implements InboundPacket

    public static final PacketDecoder<Packet7UseEntity> DECODER = Packet7UseEntity::new; // Poseidon

    public int a;
    public int target;
    public int c;

    public Packet7UseEntity() {}

    // Poseidon start
    public Packet7UseEntity(DataInput input) throws IOException {
        this();
        a(input);
    }
    // Poseidon end

    public void a(DataInput datainputstream) throws IOException {
        this.a = datainputstream.readInt();
        this.target = datainputstream.readInt();
        this.c = datainputstream.readByte();
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeInt(this.a);
        dataoutputstream.writeInt(this.target);
        dataoutputstream.writeByte(this.c);
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        return 9;
    }
}
