package net.minecraft.server;

import com.legacyminecraft.poseidon.Poseidon;
import com.legacyminecraft.poseidon.network.login.LoginProcessHandler;
import com.legacyminecraft.poseidon.network.login.LoginState;
import com.legacyminecraft.poseidon.profile.MinecraftProfile;
import org.jspecify.annotations.Nullable;

import java.net.Socket;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class NetLoginHandler extends NetHandler {

    public static Logger a = Logger.getLogger("Minecraft");
    private static Random d = new Random();
    public NetworkManager networkManager;
    public AtomicBoolean c = new AtomicBoolean(false); // Poseidon - boolean -> AtomicBoolean
    private MinecraftServer server;
    private int f = 0;
    private @Nullable String g = null;
    private volatile @Nullable MinecraftProfile h = null; // Poseidon - volatile, Packet1Login -> MinecraftProfile
    private String i = "";

    // Poseidon start
    private static final Executor ASYNC_EXECUTOR = Executors.newCachedThreadPool(
            Thread.ofPlatform().name("LoginThread-", 1).factory());
    // Poseidon end

    public NetLoginHandler(MinecraftServer minecraftserver, Socket socket, String s) {
        this.server = minecraftserver;
        this.networkManager = new NetworkManager(socket, s, this);
        this.networkManager.f = 0;
    }

    // CraftBukkit start
    public @Nullable Socket getSocket() {
        return this.networkManager.socket;
    }
    // CraftBukkit end

    // Poseidon start
    public MinecraftServer getServer() {
        return this.server;
    }

    public boolean isConnected() {
        return !this.c.get();
    }
    // Poseidon end

    public void a() {
        if (this.h != null) {
            this.b(this.h);
            this.h = null;
        }

        if (this.f++ == 600) {
            this.disconnect("Took too long to log in");
        } else {
            this.networkManager.b();
        }
    }

    public void disconnect(String s) {
        // Poseidon start
        if (!this.c.compareAndSet(false, true)) {
            return;
        }
        // Poseidon end

        try {
            a.info("Disconnecting " + this.b() + ": " + s);
            // Poseidon start - truncate kick message to 100 characters
            String message = s.substring(0, Math.min(s.length(), 100));
            this.networkManager.queue(new Packet255KickDisconnect(message));
            // Poseidon end
            this.networkManager.d();
            //this.c = true; // Poseidon
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void a(Packet2Handshake packet2handshake) {
        // Poseidon start
        LoginState loginState = this.networkManager.getLoginState();
        if (loginState != LoginState.INITIAL && loginState != LoginState.PROXY) {
            disconnect("Unexpected handshake packet");
            return;
        }
        this.networkManager.setLoginState(LoginState.HANDSHAKE);
        // Poseidon end

        if (this.server.onlineMode) {
            this.i = Long.toHexString(d.nextLong());
            this.networkManager.queue(new Packet2Handshake(this.i));
        } else {
            this.networkManager.queue(new Packet2Handshake("-"));
        }
    }

    public void a(Packet1Login packet1login) {
        // Poseidon start
        LoginState loginState = this.networkManager.getLoginState();
        if (loginState != LoginState.HANDSHAKE) {
            disconnect("Unexpected login packet");
            return;
        }
        this.networkManager.setLoginState(LoginState.LOGIN);
        // Poseidon end

        this.g = packet1login.name;
        if (packet1login.a != 14) {
            if (packet1login.a > 14) {
                this.disconnect("Outdated server!");
            } else {
                this.disconnect("Outdated client!");
            }
        // Poseidon start
        } else if (Poseidon.getConfig().network.proxySupport.enabled
                && Poseidon.getConfig().network.proxySupport.proxyRequiredToConnect
                && !this.networkManager.isProxyConnection()) {
            disconnect("You must connect through a proxy to join this server");
            // Poseidon end
        } else {
            ASYNC_EXECUTOR.execute(new LoginProcessHandler(this.server, this, packet1login.name)); // Poseidon
        }
    }

    public void b(MinecraftProfile profile) { // Poseidon - change signature
        this.g = profile.name(); // Poseidon
        EntityPlayer entityplayer = this.server.serverConfigurationManager.a(this, profile); // Poseidon - pass profile

        if (entityplayer != null) {
            this.server.serverConfigurationManager.b(entityplayer);
            // entityplayer.a((World) this.server.a(entityplayer.dimension)); // CraftBukkit - set by Entity
            // CraftBukkit - add world and location to 'logged in' message.
            a.info(this.b() + " logged in with entity id " + entityplayer.id + " at ([" + entityplayer.world.worldData.name + "] " + entityplayer.locX + ", " + entityplayer.locY + ", " + entityplayer.locZ + ")");
            WorldServer worldserver = (WorldServer) entityplayer.world; // CraftBukkit
            ChunkCoordinates chunkcoordinates = worldserver.getSpawn();
            NetServerHandler netserverhandler = new NetServerHandler(this.server, this.networkManager, entityplayer);

            netserverhandler.sendPacket(new Packet1Login("", entityplayer.id, worldserver.getSeed(), (byte) worldserver.worldProvider.dimension));
            netserverhandler.sendPacket(new Packet6SpawnPosition(chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z));
            this.server.serverConfigurationManager.a(entityplayer, worldserver);
            // this.server.serverConfigurationManager.sendAll(new Packet3Chat("\u00A7e" + entityplayer.name + " joined the game."));  // CraftBukkit - message moved to join event
            this.server.serverConfigurationManager.c(entityplayer);
            netserverhandler.a(entityplayer.locX, entityplayer.locY, entityplayer.locZ, entityplayer.yaw, entityplayer.pitch);
            this.server.networkListenThread.a(netserverhandler);
            netserverhandler.sendPacket(new Packet4UpdateTime(entityplayer.getPlayerTime())); // CraftBukkit - add support for player specific time
            entityplayer.syncInventory();
        }

        this.c.set(true); // Poseidon
    }

    public void a(String s, Object @Nullable [] aobject) {
        // Poseidon start
        if (!this.c.compareAndSet(false, true)) {
            return;
        }
        // Poseidon end

        a.info(this.b() + " lost connection");
    }

    public void a(Packet packet) {
        this.disconnect("Protocol error");
    }

    public String b() {
        return this.g != null ? this.g + " [" + this.networkManager.getSocketAddress() + "]" : this.networkManager.getSocketAddress().toString();
    }

    public boolean c() {
        return true;
    }

    public static String a(NetLoginHandler netloginhandler) { // Poseidon - public
        return netloginhandler.i;
    }

    // Poseidon - public, Packet1Login -> MinecraftProfile
    public static MinecraftProfile a(NetLoginHandler netloginhandler, MinecraftProfile profile) {
        return netloginhandler.h = profile;
    }
}
