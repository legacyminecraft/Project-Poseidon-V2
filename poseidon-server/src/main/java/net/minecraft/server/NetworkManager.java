package net.minecraft.server;

import com.google.common.base.Preconditions;
import com.legacyminecraft.poseidon.Poseidon;
import com.legacyminecraft.poseidon.event.network.ServerReceivePacketEvent;
import com.legacyminecraft.poseidon.event.network.ServerSendPacketEvent;
import com.legacyminecraft.poseidon.network.connection.AbstractPlayerConnection;
import com.legacyminecraft.poseidon.network.protocol.InboundPacket;
import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import org.jspecify.annotations.Nullable;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public class NetworkManager extends AbstractPlayerConnection { // Poseidon - extends AbstractPlayerConnection

    public static final Object a = new Object();
    public static int b;
    public static int c;
    private Object g = new Object();
    public @Nullable Socket socket; // CraftBukkit - private -> public
    private InetSocketAddress i; // Poseidon - not final, SocketAddress -> InetSocketAddress
    private @Nullable DataInputStream input;
    private @Nullable DataOutputStream output;
    private boolean l = true;
    //private List<Packet> m = Collections.synchronizedList(new ArrayList<>()); // Poseidon - remove
    private Queue<QueuedPacket> highPriorityQueue = new ConcurrentLinkedQueue<>(); // Poseidon - List<Packet> -> ConcurrentLinkedQueue<QueuedPacket>
    private Queue<QueuedPacket> lowPriorityQueue = new ConcurrentLinkedQueue<>(); // Poseidon - List<Packet> -> ConcurrentLinkedQueue<QueuedPacket>
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

    // Poseidon start
    private final InetSocketAddress rawAddress;
    private final AtomicLong readPackets = new AtomicLong(0L);
    private long lastReadPackets = 0L;
    // Poseidon end

    public NetworkManager(Socket socket, String s, NetHandler nethandler) {
        this.socket = socket;
        this.i = (InetSocketAddress) socket.getRemoteSocketAddress();
        this.rawAddress = this.i; // Poseidon
        this.p = nethandler;

        // CraftBukkit start - IPv6 stack in Java on BSD/OSX doesn't support setTrafficClass
        try {
            socket.setTrafficClass(24);
        } catch (SocketException e) {}
        // CraftBukkit end

        try {
            // CraftBukkit start - cant compile these outside the try
            socket.setSoTimeout((int) Poseidon.getConfig().network.timeout.getMillis()); // Poseidon - configurable connection timeout
            socket.setTcpNoDelay(true); // Poseidon - disable Nagle's algorithm
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
        // Poseidon start
        /*this.s.start();
        this.r.start();*/
    }

    void startThreads() {
        this.s.start();
        this.r.start();
    }

    @Override
    public NetHandler getNetHandler() {
        return this.p;
    }
    // Poseidon end

    public void a(NetHandler nethandler) {
        this.p = nethandler;
    }

    // Poseidon start - network API
    @Override
    public void sendPacket(OutboundPacket packet) {
        queue(packet);
    }

    @Override
    public InetSocketAddress getRawAddress() {
        return this.rawAddress;
    }

    @Override
    public InetSocketAddress getClientAddress() {
        return getSocketAddress();
    }

    @Override
    public void setClientAddress(InetSocketAddress address) {
        Preconditions.checkArgument(address != null, "address cannot be null");
        this.i = address;
    }
    // Poseidon end

    // Poseidon start - change signature
    public void queue(OutboundPacket packet) {
        Preconditions.checkArgument(packet != null, "packet cannot be null");

        if (!this.q) {
            //this.x += packet.a() + 1;
            QueuedPacket queuedPacket = new QueuedPacket(packet, System.nanoTime());
            if (packet instanceof Packet nmsPacket && nmsPacket.k) {
                this.lowPriorityQueue.add(queuedPacket);
            } else {
                this.highPriorityQueue.add(queuedPacket);
            }
            this.a();
        }
    }

    private record QueuedPacket(OutboundPacket packet, long timestamp) {
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
            if ((queuedPacket = this.highPriorityQueue.poll()) != null) {
                //this.x -= packet.a() + 1;

                OutboundPacket packet = queuedPacket.packet();
                boolean send = new ServerSendPacketEvent(this, queuedPacket.packet()).callEvent();
                if (send) {
                    Poseidon.getProtocolManager().encodePacket(packet, this.output);
                }
                /*aint = e;
                i = packet.b();
                aint[i] += packet.a() + 1;*/
                // Poseidon end
                flag = true;
            }

            // CraftBukkit - don't allow low priority packet to be sent unless it was placed in the queue before the first packet on the high priority queue
            // Poseidon start
            if ((flag || this.lowPriorityQueueDelay-- <= 0) && !this.lowPriorityQueue.isEmpty() && (this.highPriorityQueue.isEmpty() || this.highPriorityQueue.peek().timestamp > this.lowPriorityQueue.peek().timestamp)) {
                queuedPacket = this.lowPriorityQueue.poll();
                //this.x -= packet.a() + 1;

                OutboundPacket packet = queuedPacket.packet();
                boolean send = new ServerSendPacketEvent(this, queuedPacket.packet()).callEvent();
                if (send) {
                    Poseidon.getProtocolManager().encodePacket(packet, this.output);
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
                handleReadPacket(packet);
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

    // Poseidon start
    private void handleReadPacket(InboundPacket packet) {
        this.readPackets.incrementAndGet();

        boolean handle = new ServerReceivePacketEvent(this, packet).callEvent();
        if (handle && packet instanceof Packet nmsPacket) {
            NetHandler netHandler = this.p;
            netHandler.getServer().queueSyncTask(() -> nmsPacket.a(netHandler));
        }
    }
    // Poseidon end

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
        // Poseidon start
        long readPackets = this.readPackets.get();
        long newPackets = readPackets - this.lastReadPackets;
        this.lastReadPackets = readPackets;
        // Poseidon end

        if (this.x > 1048576) {
            this.a("disconnect.overflow");
        }

        if (newPackets == 0) { // Poseidon
            if (this.w++ == 1200) {
                this.a("disconnect.timeout");
            }
        } else {
            this.w = 0;
        }

        // Poseidon start - move packet handling to handleReadPacket()
        /*int i = 100;

        while (!this.m.isEmpty() && i-- >= 0) {
            Packet packet = this.m.remove(0);

            packet.a(this.p);
        }*/
        // Poseidon end

        this.a();
        if (this.t && newPackets == 0) { // Poseidon
            this.p.a(this.u, this.v);
        }
    }

    public InetSocketAddress getSocketAddress() { // Poseidon - SocketAddress -> InetSocketAddress
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
