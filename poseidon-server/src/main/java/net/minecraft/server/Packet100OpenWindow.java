package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketEncoder;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet100OpenWindow extends Packet implements OutboundPacket { // Poseidon - implements OutboundPacket

    public static final PacketEncoder<Packet100OpenWindow> ENCODER = Packet100OpenWindow::a; // Poseidon

    public int a;
    public int b;
    public String c;
    public int d;

    public Packet100OpenWindow() {}

    public Packet100OpenWindow(int i, int j, String s, int k) {
        this.a = i;
        this.b = j;
        this.c = s;
        this.d = k;
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public void a(DataInput datainputstream) throws IOException {
        this.a = datainputstream.readByte();
        this.b = datainputstream.readByte();
        this.c = datainputstream.readUTF();
        this.d = datainputstream.readByte();
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeByte(this.a);
        dataoutputstream.writeByte(this.b);
        dataoutputstream.writeUTF(this.c);
        dataoutputstream.writeByte(this.d);
    }

    public int a() {
        return 3 + this.c.length();
    }
}
