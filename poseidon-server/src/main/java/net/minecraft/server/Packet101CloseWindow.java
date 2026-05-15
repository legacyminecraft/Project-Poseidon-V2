package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.DuplexPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketCodec;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet101CloseWindow extends Packet implements DuplexPacket { // Poseidon - implements DuplexPacket

    // Poseidon start
    public static final PacketCodec<Packet101CloseWindow> CODEC = PacketCodec.of(
            Packet101CloseWindow::a, Packet101CloseWindow::new
    );
    // Poseidon end

    public int a;

    public Packet101CloseWindow() {}

    public Packet101CloseWindow(int i) {
        this.a = i;
    }

    // Poseidon start
    public Packet101CloseWindow(DataInput input) throws IOException {
        this();
        a(input);
    }
    // Poseidon end

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public void a(DataInput datainputstream) throws IOException {
        this.a = datainputstream.readByte();
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeByte(this.a);
    }

    public int a() {
        return 1;
    }
}
