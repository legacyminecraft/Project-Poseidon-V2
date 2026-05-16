package net.minecraft.server;

import com.legacyminecraft.poseidon.Poseidon;
import com.legacyminecraft.poseidon.network.connection.AbstractPlayerConnection;
import com.legacyminecraft.poseidon.network.connection.ConnectionFuture;
import com.legacyminecraft.poseidon.network.connection.ConnectionFutureImpl;
import com.legacyminecraft.poseidon.network.protocol.InboundPacket;
import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import org.jspecify.annotations.Nullable;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NetworkManager extends AbstractPlayerConnection { // Poseidon - extends AbstractPlayerConnection

    public static final Object a = new Object();
    public static int b;
    public static int c;
    private Object g = new Object();
    public @Nullable Socket socket; // CraftBukkit - private -> public
    private final SocketAddress i;
    private @Nullable DataInputStream input;
    private @Nullable DataOutputStream output;
    private boolean l = true;
    private List<Packet> m = Collections.synchronizedList(new ArrayList<>());
    private List<QueuedPacket> highPriorityQueue = Collections.synchronizedList(new ArrayList<>()); // Poseidon - List<Packet> -> List<QueuedPacket>
    private List<QueuedPacket> lowPriorityQueue = Collections.synchronizedList(new ArrayList<>()); // Poseidon - List<Packet> -> List<QueuedPacket>
    private NetHandler p;
    private boolean q = false;
    private Thread r;
    private Thread s;
    private boolean t = false;
    private String u = "";
    private Object @Nullable [] v;
    private int w = 0;
    private int x = 0;
    public static int[] d = new int[256];
    public static int[] e = new int[256];
    public int f = 0;
    private int lowPriorityQueueDelay = 50;

    public NetworkManager(Socket socket, String s, NetHandler nethandler) {
        this.socket = socket;
        this.i = socket.getRemoteSocketAddress();
        this.p = nethandler;

        // CraftBukkit start - IPv6 stack in Java on BSD/OSX doesn't support setTrafficClass
        try {
            socket.setTrafficClass(24);
        } catch (SocketException e) {}
        // CraftBukkit end

        try {
            // CraftBukkit start - cant compile these outside the try
            socket.setSoTimeout(30000);
            this.input = new DataInputStream(socket.getInputStream());
            this.output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream(), 5120));
        } catch (java.io.IOException socketexception) {
            // CraftBukkit end
            System.err.println(socketexception.getMessage());
        }

        /* CraftBukkit start - moved up
        this.input = new DataInputStream(socket.getInputStream());
        this.output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream(), 5120));
        // CraftBukkit end */
        this.s = new NetworkReaderThread(this, s + " read thread");
        this.r = new NetworkWriterThread(this, s + " write thread");
        this.s.start();
        this.r.start();
    }

    public void a(NetHandler nethandler) {
        this.p = nethandler;
    }

    // Poseidon start - network API
    private record QueuedPacket(OutboundPacket packet, ConnectionFutureImpl future, long timestamp) {
    }

    @Override
    public ConnectionFuture sendPacket(OutboundPacket packet) {
        return queue(packet);
    }

    @Override
    public void disconnect(String message) {
        this.p.disconnect(message);
    }

    @Override
    public boolean isProxyConnection() {
        return false; // TODO: implement proxy support
    }

    @Override
    public InetSocketAddress getRawAddress() {
        return (InetSocketAddress) getSocketAddress();
    }

    @Override
    public InetSocketAddress getClientAddress() {
        return getRawAddress(); // TODO: implement proxy support
    }
    // Poseidon end

    // Poseidon start - change signature, return ConnectionFuture
    public ConnectionFuture queue(OutboundPacket packet) {
        ConnectionFutureImpl future = new ConnectionFutureImpl(this);
        if (!this.q) {
            synchronized (this.g) {
                //this.x += packet.a() + 1;
                QueuedPacket queuedPacket = new QueuedPacket(packet, future, System.currentTimeMillis());
                if (packet instanceof Packet nmsPacket && nmsPacket.k) {
                    this.lowPriorityQueue.add(queuedPacket);
                } else {
                    this.highPriorityQueue.add(queuedPacket);
                }
            }
        }
        return future;
    }
    // Poseidon end

    private boolean f() {
        boolean flag = false;

        try {
            Object object;
            QueuedPacket queuedPacket; // Poseidon
            int i;
            int[] aint;

            // Poseidon start
            if (!this.highPriorityQueue.isEmpty()) {
                synchronized (this.g) {
                    queuedPacket = this.highPriorityQueue.remove(0);
                    //this.x -= packet.a() + 1;
                }

                OutboundPacket packet = invokeOutboundHandlers(queuedPacket.packet());
                if (packet != null) {
                    Poseidon.getProtocolManager().encodePacket(packet, this.output);
                    queuedPacket.future().complete();
                }
                /*aint = e;
                i = packet.b();
                aint[i] += packet.a() + 1;*/
                // Poseidon end
                flag = true;
            }

            // CraftBukkit - don't allow low priority packet to be sent unless it was placed in the queue before the first packet on the high priority queue
            if ((flag || this.lowPriorityQueueDelay-- <= 0) && !this.lowPriorityQueue.isEmpty() && (this.highPriorityQueue.isEmpty() || this.highPriorityQueue.get(0).timestamp > this.lowPriorityQueue.get(0).timestamp)) {
                // Poseidon start
                synchronized (this.g) {
                    queuedPacket = this.lowPriorityQueue.remove(0);
                    //this.x -= packet.a() + 1;
                }

                OutboundPacket packet = invokeOutboundHandlers(queuedPacket.packet());
                if (packet != null) {
                    Poseidon.getProtocolManager().encodePacket(packet, this.output);
                    queuedPacket.future().complete();
                }
                /*aint = e;
                i = packet.b();
                aint[i] += packet.a() + 1;*/
                // Poseidon end
                this.lowPriorityQueueDelay = 0;
                flag = true;
            }

            return flag;
        } catch (Exception exception) {
            if (!this.t) {
                this.a(exception);
            }

            return false;
        }
    }

    public void a() {
        this.s.interrupt();
        this.r.interrupt();
    }

    private boolean g() {
        boolean flag = false;

        try {
            // Poseidon start
            InboundPacket packet = Poseidon.getProtocolManager().decodePacket(this.input);

            if (packet != null) {
                /*int[] aint = d;
                int i = packet.b();

                aint[i] += packet.a() + 1;*/
                packet = invokeInboundHandlers(packet);
                if (packet instanceof Packet nmsPacket) {
                    this.m.add(nmsPacket);
                }
                // Poseidon end
                flag = true;
            } else {
                this.a("disconnect.endOfStream");
            }

            return flag;
        } catch (Exception exception) {
            if (!this.t) {
                this.a(exception);
            }

            return false;
        }
    }

    private void a(Exception exception) {
        exception.printStackTrace();
        this.a("disconnect.genericReason", "Internal exception: " + exception);
    }

    public void a(String s, Object... aobject) {
        if (this.l) {
            this.t = true;
            this.u = s;
            this.v = aobject;
            (new NetworkMasterThread(this)).start();
            this.l = false;

            try {
                this.input.close();
                this.input = null;
            } catch (Throwable throwable) {
                ;
            }

            try {
                this.output.close();
                this.output = null;
            } catch (Throwable throwable1) {
                ;
            }

            try {
                this.socket.close();
                this.socket = null;
            } catch (Throwable throwable2) {
                ;
            }
        }
    }

    public void b() {
        if (this.x > 1048576) {
            this.a("disconnect.overflow");
        }

        if (this.m.isEmpty()) {
            if (this.w++ == 1200) {
                this.a("disconnect.timeout");
            }
        } else {
            this.w = 0;
        }

        int i = 100;

        while (!this.m.isEmpty() && i-- >= 0) {
            Packet packet = this.m.remove(0);

            packet.a(this.p);
        }

        this.a();
        if (this.t && this.m.isEmpty()) {
            this.p.a(this.u, this.v);
        }
    }

    public SocketAddress getSocketAddress() {
        return this.i;
    }

    public void d() {
        this.a();
        this.q = true;
        this.s.interrupt();
        (new ThreadMonitorConnection(this)).start();
    }

    public int e() {
        return this.lowPriorityQueue.size();
    }

    static boolean a(NetworkManager networkmanager) {
        return networkmanager.l;
    }

    static boolean b(NetworkManager networkmanager) {
        return networkmanager.q;
    }

    static boolean c(NetworkManager networkmanager) {
        return networkmanager.g();
    }

    static boolean d(NetworkManager networkmanager) {
        return networkmanager.f();
    }

    static DataOutputStream e(NetworkManager networkmanager) {
        return networkmanager.output;
    }

    static boolean f(NetworkManager networkmanager) {
        return networkmanager.t;
    }

    static void a(NetworkManager networkmanager, Exception exception) {
        networkmanager.a(exception);
    }

    static Thread g(NetworkManager networkmanager) {
        return networkmanager.s;
    }

    static Thread h(NetworkManager networkmanager) {
        return networkmanager.r;
    }
}
