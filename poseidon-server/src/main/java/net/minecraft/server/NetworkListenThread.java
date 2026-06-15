package net.minecraft.server;

import com.legacyminecraft.poseidon.network.connection.ConnectionManager;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkListenThread implements ConnectionManager<NetworkManager> { // Poseidon - implements ConnectionManager<NetworkManager>

    public static Logger a = Logger.getLogger("Minecraft");
    private ServerSocket d;
    private Thread e;
    public volatile boolean b = false;
    private int f = 0;
    private List<NetworkManager> g = new CopyOnWriteArrayList<>(); // Poseidon - ArrayList<NetLoginHandler> -> CopyOnWriteArrayList<NetworkManager>
    //private ArrayList<NetServerHandler> h = new ArrayList<>(); // Poseidon - remove
    public MinecraftServer c;

    public NetworkListenThread(MinecraftServer minecraftserver, @Nullable InetAddress inetaddress, int i) throws IOException {
        this.c = minecraftserver;
        this.d = new ServerSocket(i, 0, inetaddress);
        this.d.setPerformancePreferences(0, 2, 1);
        this.b = true;
        this.e = new NetworkAcceptThread(this, "Listen thread", minecraftserver);
        this.e.start();
    }

    // Poseidon start - remove
    /*public void a(NetServerHandler netserverhandler) {
        this.h.add(netserverhandler);
    }*/
    // Poseidon end

    private void a(NetLoginHandler netloginhandler) {
        if (netloginhandler == null) {
            throw new IllegalArgumentException("Got null pendingconnection!");
        } else {
            this.g.add((NetworkManager) netloginhandler.networkManager); // Poseidon
        }
    }

    // Poseidon start
    public Iterable<NetworkManager> getConnections() {
        return this.g::iterator;
    }

    public void tickConnections() {
        a();
    }
    // Poseidon end

    public void a() {
        // Poseidon start
        for (NetworkManager networkManager : this.g) {
            try {
                networkManager.getNetHandler().tick();
            } catch (Exception e) {
                a.log(Level.WARNING, "Failed to tick connection", e);
                networkManager.disconnect("Internal server error");
            }

            if (!networkManager.isConnected()) {
                this.g.remove(networkManager);
            }
        }

        /*for (i = 0; i < this.h.size(); ++i) {
            NetServerHandler netserverhandler = this.h.get(i);

            try {
                netserverhandler.a();
            } catch (Exception exception1) {
                a.log(Level.WARNING, "Failed to handle packet: " + exception1, exception1);
                netserverhandler.disconnect("Internal server error");
            }

            if (netserverhandler.disconnected.get()) {
                this.h.remove(i--);
            }

            netserverhandler.networkManager.a();
        }*/
        // Poseidon end
    }

    static ServerSocket a(NetworkListenThread networklistenthread) {
        return networklistenthread.d;
    }

    static int b(NetworkListenThread networklistenthread) {
        return networklistenthread.f++;
    }

    static void a(NetworkListenThread networklistenthread, NetLoginHandler netloginhandler) {
        networklistenthread.a(netloginhandler);
    }
}
