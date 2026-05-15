package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.InboundPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketDecoder;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet16BlockItemSwitch extends Packet implements InboundPacket { // Poseidon - implements InboundPacket

    public static final PacketDecoder<Packet16BlockItemSwitch> DECODER = Packet16BlockItemSwitch::new; // Poseidon

    public int itemInHandIndex;

    public Packet16BlockItemSwitch() {}

    // Poseidon start
    public Packet16BlockItemSwitch(DataInput input) throws IOException {
        this();
        a(input);
    }
    // Poseidon end

    public void a(DataInput datainputstream) throws IOException {
        this.itemInHandIndex = datainputstream.readShort();
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeShort(this.itemInHandIndex);
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        return 2;
    }
}
