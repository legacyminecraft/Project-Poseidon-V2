package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketEncoder;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet105CraftProgressBar extends Packet implements OutboundPacket { // Poseidon - implements OutboundPacket

    public static final PacketEncoder<Packet105CraftProgressBar> ENCODER = Packet105CraftProgressBar::a; // Poseidon

    public int a;
    public int b;
    public int c;

    public Packet105CraftProgressBar() {}

    public Packet105CraftProgressBar(int i, int j, int k) {
        this.a = i;
        this.b = j;
        this.c = k;
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public void a(DataInput datainputstream) throws IOException {
        this.a = datainputstream.readByte();
        this.b = datainputstream.readShort();
        this.c = datainputstream.readShort();
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeByte(this.a);
        dataoutputstream.writeShort(this.b);
        dataoutputstream.writeShort(this.c);
    }

    public int a() {
        return 5;
    }
}
