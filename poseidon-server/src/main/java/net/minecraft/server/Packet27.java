package net.minecraft.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet27 extends Packet {

    private float a;
    private float b;
    private boolean c;
    private boolean d;
    private float e;
    private float f;

    public Packet27() {}

    public void a(DataInput datainputstream) throws IOException {
        this.a = datainputstream.readFloat();
        this.b = datainputstream.readFloat();
        this.e = datainputstream.readFloat();
        this.f = datainputstream.readFloat();
        this.c = datainputstream.readBoolean();
        this.d = datainputstream.readBoolean();
    }

    public void a(DataOutput dataoutputstream) throws IOException {
        dataoutputstream.writeFloat(this.a);
        dataoutputstream.writeFloat(this.b);
        dataoutputstream.writeFloat(this.e);
        dataoutputstream.writeFloat(this.f);
        dataoutputstream.writeBoolean(this.c);
        dataoutputstream.writeBoolean(this.d);
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        return 18;
    }

    public float c() {
        return this.a;
    }

    public float d() {
        return this.e;
    }

    public float e() {
        return this.b;
    }

    public float f() {
        return this.f;
    }

    public boolean g() {
        return this.c;
    }

    public boolean h() {
        return this.d;
    }
}
