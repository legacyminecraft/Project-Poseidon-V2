package net.minecraft.server;

import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketEncoder;
import com.legacyminecraft.poseidon.util.BlockPos;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Set;

public class Packet60Explosion extends Packet implements OutboundPacket { // Poseidon - implements OutboundPacket

    public static final PacketEncoder<Packet60Explosion> ENCODER = Packet60Explosion::a; // Poseidon

    public double a;
    public double b;
    public double c;
    public float d;
    public LongOpenHashSet e; // Poseidon - Set -> LongOpenHashSet

    public Packet60Explosion() {}

    public Packet60Explosion(double d0, double d1, double d2, float f, Set<ChunkPosition> set) {
        this.a = d0;
        this.b = d1;
        this.c = d2;
        this.d = f;
        this.e = new LongOpenHashSet(set.stream().map(cpos -> BlockPos.of(cpos.x, cpos.y, cpos.z)).toList()); // Poseidon
    }

    // Poseidon start
    public Packet60Explosion(double d0, double d1, double d2, float f, LongOpenHashSet set) {
        this.a = d0;
        this.b = d1;
        this.c = d2;
        this.d = f;
        this.e = set.clone();
    }
    // Poseidon end

    public void a(DataInput datainputstream) throws IOException {
        this.a = datainputstream.readDouble();
        this.b = datainputstream.readDouble();
        this.c = datainputstream.readDouble();
        this.d = datainputstream.readFloat();
        int i = datainputstream.readInt();

        this.e = new LongOpenHashSet(); // Poseidon
        int j = (int) this.a;
        int k = (int) this.b;
        int l = (int) this.c;

        for (int i1 = 0; i1 < i; ++i1) {
            int j1 = datainputstream.readByte() + j;
            int k1 = datainputstream.readByte() + k;
            int l1 = datainputstream.readByte() + l;

            this.e.add(BlockPos.of(j1, k1, l1)); // Poseidon
        }
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeDouble(this.a);
        dataoutputstream.writeDouble(this.b);
        dataoutputstream.writeDouble(this.c);
        dataoutputstream.writeFloat(this.d);
        dataoutputstream.writeInt(this.e.size());
        int i = (int) this.a;
        int j = (int) this.b;
        int k = (int) this.c;

        // Poseidon start - ChunkPosition -> long
        LongIterator iterator = this.e.iterator();

        while (iterator.hasNext()) {
            long blockPos = iterator.nextLong();
            int l = BlockPos.x(blockPos) - i;
            int i1 = BlockPos.y(blockPos) - j;
            int j1 = BlockPos.z(blockPos) - k;
            // Poseidon end

            dataoutputstream.writeByte(l);
            dataoutputstream.writeByte(i1);
            dataoutputstream.writeByte(j1);
        }
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        return 32 + this.e.size() * 3;
    }
}
