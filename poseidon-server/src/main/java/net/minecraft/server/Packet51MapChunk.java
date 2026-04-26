package net.minecraft.server;

import com.legacyminecraft.poseidon.world.ChunkSection;
import org.jspecify.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Packet51MapChunk extends Packet {

    public int a;
    public int b;
    public int c;
    public int d;
    public int e;
    public int f;
    public byte @Nullable [] g;
    public int h; // CraftBukkit - private -> public
    public byte[] rawData; // CraftBukkit

    private static final ThreadLocal<Deflater> localDeflater = ThreadLocal.withInitial(Deflater::new); // Poseidon

    public Packet51MapChunk() {
        this.k = true;
    }

    // Poseidon start
    public Packet51MapChunk(Chunk chunk) {
        int height = calculateHeight(chunk);
        this(chunk.x << 4, 0, chunk.z << 4, 16, height, 16, getChunkData(chunk, height));
    }
    // Poseidon end

    // CraftBukkit start
    public Packet51MapChunk(int i, int j, int k, int l, int i1, int j1, World world) {
        this(i, j, k, l, i1, j1, world.getMultiChunkData(i, j, k, l, i1, j1));
    }

    public Packet51MapChunk(int i, int j, int k, int l, int i1, int j1, byte[] data) {
        // CraftBukkit end
        this.k = true;
        this.a = i;
        this.b = j;
        this.c = k;
        this.d = l;
        this.e = i1;
        this.f = j1;
        /* CraftBukkit - Moved compression into its own method.
        byte[] abyte = data; // CraftBukkit - uses data from above constructor
        Deflater deflater = new Deflater(-1);

        try {
            deflater.setInput(abyte);
            deflater.finish();
            this.g = new byte[l * i1 * j1 * 5 / 2];
            this.h = deflater.deflate(this.g);
        } finally {
            deflater.end();
        }*/
        this.rawData = data; // CraftBukkit
    }

    // Poseidon start - handle chunk compression when writing packet
    private synchronized void compress() {
        if (this.g != null) return;

        byte[] deflateBuffer = new byte[this.rawData.length + 100];
        Deflater deflater = localDeflater.get();
        deflater.reset();
        deflater.setLevel(Deflater.DEFAULT_COMPRESSION); // TODO: make compression level configurable
        deflater.setInput(this.rawData);
        deflater.finish();

        int size = deflater.deflate(deflateBuffer);
        if (size == 0) {
            size = deflater.deflate(deflateBuffer);
        }

        this.g = new byte[size];
        this.h = size;
        System.arraycopy(deflateBuffer, 0, this.g, 0, size);
    }
    // Poseidon end

    public void a(DataInputStream datainputstream) throws IOException { // CraftBukkit - throws IOException
        this.a = datainputstream.readInt();
        this.b = datainputstream.readShort();
        this.c = datainputstream.readInt();
        this.d = datainputstream.read() + 1;
        this.e = datainputstream.read() + 1;
        this.f = datainputstream.read() + 1;
        this.h = datainputstream.readInt();
        byte[] abyte = new byte[this.h];

        datainputstream.readFully(abyte);
        this.g = new byte[this.d * this.e * this.f * 5 / 2];
        Inflater inflater = new Inflater();

        inflater.setInput(abyte);

        try {
            inflater.inflate(this.g);
        } catch (DataFormatException dataformatexception) {
            throw new IOException("Bad compressed data format");
        } finally {
            inflater.end();
        }
    }

    public void a(DataOutputStream dataoutputstream) throws IOException { // CraftBukkit - throws IOException
        compress(); // Poseidon
        dataoutputstream.writeInt(this.a);
        dataoutputstream.writeShort(this.b);
        dataoutputstream.writeInt(this.c);
        dataoutputstream.write(this.d - 1);
        dataoutputstream.write(this.e - 1);
        dataoutputstream.write(this.f - 1);
        dataoutputstream.writeInt(this.h);
        dataoutputstream.write(this.g, 0, this.h);
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        return 17 + this.h;
    }

    // Poseidon start
    private static int calculateHeight(Chunk chunk) {
        ChunkSection[] sections = chunk.getSections();
        int yPos = sections.length;
        while (yPos > 0 && !sections[yPos - 1].hasBlocks()) {
            yPos--;
        }
        return yPos << 4;
    }

    private static byte[] getChunkData(Chunk chunk, int height) {
        byte[] data = new byte[16 * height * 16 * 5 / 2];
        chunk.getData(data, 0, 0, 0, 16, height, 16, 0);
        return data;
    }
    // Poseidon end
}
