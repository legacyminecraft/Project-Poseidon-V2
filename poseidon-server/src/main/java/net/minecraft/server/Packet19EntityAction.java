package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.InboundPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketDecoder;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet19EntityAction extends Packet implements InboundPacket { // Poseidon - implements InboundPacket

    public static final PacketDecoder<Packet19EntityAction> DECODER = Packet19EntityAction::new; // Poseidon

    public int a;
    public int animation;

    public Packet19EntityAction() {}

    // Poseidon start
    public Packet19EntityAction(DataInput input) throws IOException {
        this();
        a(input);
    }
    // Poseidon end

    public void a(DataInput datainputstream) throws IOException {
        this.a = datainputstream.readInt();
        this.animation = datainputstream.readByte();
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeInt(this.a);
        dataoutputstream.writeByte(this.animation);
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        return 5;
    }
}
