package net.minecraft.server;

import com.legacyminecraft.poseidon.Poseidon;
import com.legacyminecraft.poseidon.network.connection.AbstractPlayerConnection;
import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandException;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.TextWrapper;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class NetServerHandler extends NetHandler implements ICommandListener {

    public static Logger a = Logger.getLogger("Minecraft");
    public AbstractPlayerConnection networkManager; // Poseidon - NetworkManager -> AbstractPlayerConnection
    public AtomicBoolean disconnected = new AtomicBoolean(false); // Poseidon - boolean -> AtomicBoolean
    private MinecraftServer minecraftServer;
    public EntityPlayer player; // CraftBukkit - private -> public
    private int f;
    private int g;
    private int h;
    private boolean i;
    private double x;
    private double y;
    private double z;
    private boolean checkMovement = true;
    private Map<Integer, Short> n = new HashMap<>();

    // Poseidon start
    private final Random idGenerator = new Random();
    private int ticks = 0;
    // Poseidon end

    // Poseidon - change signature
    public NetServerHandler(MinecraftServer minecraftserver, AbstractPlayerConnection networkmanager, EntityPlayer entityplayer) {
        this.minecraftServer = minecraftserver;
        this.networkManager = networkmanager;
        networkmanager.a(this);
        this.player = entityplayer;
        entityplayer.netServerHandler = this;

        // CraftBukkit start
        this.server = minecraftserver.server;
    }
    private final CraftServer server;
    private int lastTick = MinecraftServer.currentTick;
    private int lastDropTick = MinecraftServer.currentTick;
    private int dropCount = 0;
    private static final int PLACE_DISTANCE_SQUARED = 6 * 6;

    // Get position of last block hit for BlockDamageLevel.STOPPED
    private double lastPosX = Double.MAX_VALUE;
    private double lastPosY = Double.MAX_VALUE;
    private double lastPosZ = Double.MAX_VALUE;
    private float lastPitch = Float.MAX_VALUE;
    private float lastYaw = Float.MAX_VALUE;
    private boolean justTeleported = false;

    // For the packet15 hack :(
    @Nullable Long lastPacket;

    // Store the last block right clicked and what type it was
    private int lastMaterial;

    public CraftPlayer getPlayer() {
        return (this.player == null) ? null : (CraftPlayer) this.player.getBukkitEntity();
    }
    // CraftBukkit end

    // Poseidon start
    public MinecraftServer getServer() {
        return this.minecraftServer;
    }

    public AbstractPlayerConnection getConnection() {
        return this.networkManager;
    }

    public boolean isConnected() {
        return !this.disconnected.get();
    }

    public void tick() {
        a();
    }
    // Poseidon end

    public void a() {
        this.i = false;
        this.networkManager.b();
        if (this.f - this.g > 20) {
            this.sendPacket(new Packet0KeepAlive());
        }

        // Poseidon start - ping every 2 seconds
        if (this.ticks % 40 == 0) {
            short id = (short) this.idGenerator.nextInt(Short.MIN_VALUE, 0);
            this.sendPacket(new Packet106Transaction(0, id, false));
        }
        this.ticks++;
        // Poseidon end
    }

    public void disconnect(String s) {
        // Poseidon start - allow disconnect to be initiated async
        if (!this.disconnected.compareAndSet(false, true)) {
            return;
        }

        if (this.server.isPrimaryThread()) {
            internalDisconnect(s);
        } else {
            this.server.getServer().queueSyncTask(() -> internalDisconnect(s));
        }
        // Poseidon end
    }

    // Poseidon start - move disconnect logic to separate method
    private void internalDisconnect(String s) {
        // CraftBukkit start
        String leaveMessage = "\u00A7e" + this.player.name + " left the game.";

        PlayerKickEvent event = new PlayerKickEvent(this.server.getPlayer(this.player), s, leaveMessage);
        this.server.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            // Do not kick the player
            return;
        }
        // Send the possibly modified leave message
        s = event.getReason();
        // CraftBukkit end

        this.player.B();
        // Poseidon start - truncate kick message to 100 characters
        String message = s.substring(0, Math.min(s.length(), 100));
        this.sendPacket(new Packet255KickDisconnect(message));
        // Poseidon end
        this.networkManager.d();

        // CraftBukkit start
        leaveMessage = event.getLeaveMessage();
        if (leaveMessage != null) {
            this.minecraftServer.serverConfigurationManager.sendAll(new Packet3Chat(leaveMessage));
        }
        // CraftBukkit end

        this.minecraftServer.serverConfigurationManager.disconnect(this.player);
        //this.disconnected = true; // Poseidon
    }
    // Poseidon end

    public void a(Packet27 packet27) {
        this.player.a(packet27.c(), packet27.e(), packet27.g(), packet27.h(), packet27.d(), packet27.f());
    }

    public void a(Packet10Flying packet10flying) {
        WorldServer worldserver = this.minecraftServer.getWorldServer(this.player.dimension);

        this.i = true;
        double d0;

        if (!this.checkMovement) {
            d0 = packet10flying.y - this.y;
            if (packet10flying.x == this.x && d0 * d0 < 0.01D && packet10flying.z == this.z) {
                this.checkMovement = true;
            }
        }

        // CraftBukkit start
        Player player = this.getPlayer();
        Location from = new Location(player.getWorld(), lastPosX, lastPosY, lastPosZ, lastYaw, lastPitch); // Get the Players previous Event location.
        Location to = player.getLocation().clone(); // Start off the To location as the Players current location.

        // If the packet contains movement information then we update the To location with the correct XYZ.
        if (packet10flying.h && !(packet10flying.h && packet10flying.y == -999.0D && packet10flying.stance == -999.0D)) {
            to.setX(packet10flying.x);
            to.setY(packet10flying.y);
            to.setZ(packet10flying.z);
        }

        // If the packet contains look information then we update the To location with the correct Yaw & Pitch.
        if (packet10flying.hasLook) {
            to.setYaw(packet10flying.yaw);
            to.setPitch(packet10flying.pitch);
        }

        // Prevent 40 event-calls for less than a single pixel of movement >.>
        double delta = Math.pow(this.lastPosX - to.getX(), 2) + Math.pow(this.lastPosY - to.getY(), 2) + Math.pow(this.lastPosZ - to.getZ(), 2);
        float deltaAngle = Math.abs(this.lastYaw - to.getYaw()) + Math.abs(this.lastPitch - to.getPitch());

        if ((delta > 1f / 256 || deltaAngle > 10f) && (this.checkMovement && !this.player.dead)) {
            this.lastPosX = to.getX();
            this.lastPosY = to.getY();
            this.lastPosZ = to.getZ();
            this.lastYaw = to.getYaw();
            this.lastPitch = to.getPitch();

            // Skip the first time we do this
            if (from.getX() != Double.MAX_VALUE) {
                PlayerMoveEvent event = new PlayerMoveEvent(player, from, to);
                this.server.getPluginManager().callEvent(event);

                // If the event is cancelled we move the player back to their old location.
                if (event.isCancelled()) {
                    this.player.netServerHandler.sendPacket(new Packet13PlayerLookMove(from.getX(), from.getY() + 1.6200000047683716D, from.getY(), from.getZ(), from.getYaw(), from.getPitch(), false));
                    return;
                }

                /* If a Plugin has changed the To destination then we teleport the Player
                   there to avoid any 'Moved wrongly' or 'Moved too quickly' errors.
                   We only do this if the Event was not cancelled. */
                if (!to.equals(event.getTo()) && !event.isCancelled()) {
                    this.player.getBukkitEntity().teleport(event.getTo());
                    return;
                }

                /* Check to see if the Players Location has some how changed during the call of the event.
                   This can happen due to a plugin teleporting the player instead of using .setTo() */
                if (!from.equals(this.getPlayer().getLocation()) && this.justTeleported) {
                    this.justTeleported = false;
                    return;
                }
            }
        }

        // Poseidon - fix disconnect spam
        if ((Double.isNaN(packet10flying.x) || Double.isNaN(packet10flying.y) || Double.isNaN(packet10flying.z) || Double.isNaN(packet10flying.stance)) && player.isOnline() && !this.disconnected.get()) {
            player.teleport(player.getWorld().getSpawnLocation());
            System.err.println(player.getName() + " was caught trying to crash the server with an invalid position.");
            player.kickPlayer("Nope!");
            return;
        }

        if (this.checkMovement && !this.player.dead) {
            // CraftBukkit end
            double d1;
            double d2;
            double d3;
            double d4;

            if (this.player.vehicle != null) {
                float f = this.player.yaw;
                float f1 = this.player.pitch;

                this.player.vehicle.f();
                d1 = this.player.locX;
                d2 = this.player.locY;
                d3 = this.player.locZ;
                double d5 = 0.0D;

                d4 = 0.0D;
                if (packet10flying.hasLook) {
                    f = packet10flying.yaw;
                    f1 = packet10flying.pitch;
                }

                if (packet10flying.h && packet10flying.y == -999.0D && packet10flying.stance == -999.0D) {
                    d5 = packet10flying.x;
                    d4 = packet10flying.z;

                    // Poseidon start - fix vehicle server crash exploit
                    double d8 = d5 * d5 + d4 * d4;
                    if (d8 > 100.0D) {
                        a.warning(this.player.name + " was caught trying to crash the server on entity " + this.player.vehicle.toString() + ".");
                        player.kickPlayer("Nope!");
                        return;
                    }
                    // Poseidon end
                }

                this.player.onGround = packet10flying.g;
                this.player.a(true);
                this.player.move(d5, 0.0D, d4);
                this.player.setLocation(d1, d2, d3, f, f1);
                this.player.motX = d5;
                this.player.motZ = d4;
                if (this.player.vehicle != null) {
                    worldserver.vehicleEnteredWorld(this.player.vehicle, true);
                }

                if (this.player.vehicle != null) {
                    this.player.vehicle.f();
                    this.player.vehicle.airBorne = true; // Poseidon
                }

                this.minecraftServer.serverConfigurationManager.d(this.player);
                this.x = this.player.locX;
                this.y = this.player.locY;
                this.z = this.player.locZ;
                worldserver.playerJoinedWorld(this.player);
                return;
            }

            if (this.player.isSleeping()) {
                this.player.a(true);
                this.player.setLocation(this.x, this.y, this.z, this.player.yaw, this.player.pitch);
                worldserver.playerJoinedWorld(this.player);
                return;
            }

            d0 = this.player.locY;
            this.x = this.player.locX;
            this.y = this.player.locY;
            this.z = this.player.locZ;
            d1 = this.player.locX;
            d2 = this.player.locY;
            d3 = this.player.locZ;
            float f2 = this.player.yaw;
            float f3 = this.player.pitch;

            if (packet10flying.h && packet10flying.y == -999.0D && packet10flying.stance == -999.0D) {
                packet10flying.h = false;
            }

            if (packet10flying.h) {
                d1 = packet10flying.x;
                d2 = packet10flying.y;
                d3 = packet10flying.z;
                d4 = packet10flying.stance - packet10flying.y;
                if (!this.player.isSleeping() && (d4 > 1.65D || d4 < 0.1D)) {
                    this.disconnect("Illegal stance");
                    a.warning(this.player.name + " had an illegal stance: " + d4);
                    return;
                }

                if (Math.abs(packet10flying.x) > 3.2E7D || Math.abs(packet10flying.z) > 3.2E7D) {
                    this.disconnect("Illegal position");
                    return;
                }
            }

            if (packet10flying.hasLook) {
                f2 = packet10flying.yaw;
                f3 = packet10flying.pitch;
            }

            this.player.a(true);
            this.player.br = 0.0F;
            this.player.setLocation(this.x, this.y, this.z, f2, f3);
            if (!this.checkMovement) {
                return;
            }

            d4 = d1 - this.player.locX;
            double d6 = d2 - this.player.locY;
            double d7 = d3 - this.player.locZ;
            double d8 = d4 * d4 + d6 * d6 + d7 * d7;

            // Poseidon start - configurable quick movement flagging
            if (worldserver.getConfig().anticheat.quickMovementFlagging.enabled
                    && d8 > worldserver.getConfig().anticheat.quickMovementFlagging.threshold
                    && this.checkMovement // CraftBukkit - Added this.checkMovement condition to solve this check being triggered by teleports
                    && !getPlayer().hasPermission("poseidon.anticheat.quick-movement-flagging.exempt")) {

                a.warning(this.player.name + " moved too quickly! " + d4 + "," + d6 + "," + d7 + " (" + d4 + ", " + d6 + ", " + d7 + ")");
                switch (worldserver.getConfig().anticheat.quickMovementFlagging.action) {
                    case KICK -> this.disconnect("You moved too quickly :( (Hacking?)");
                    case TELEPORT_BACK -> this.a(this.x, this.y, this.z, this.player.yaw, this.player.pitch);
                }
                return;
            }
            // Poseidon end

            float f4 = 0.0625F;
            boolean flag = worldserver.getEntities(this.player, this.player.boundingBox.clone().shrink(f4, f4, f4)).isEmpty();

            this.player.move(d4, d6, d7);
            d4 = d1 - this.player.locX;
            d6 = d2 - this.player.locY;
            if (d6 > -0.5D || d6 < 0.5D) {
                d6 = 0.0D;
            }

            d7 = d3 - this.player.locZ;
            d8 = d4 * d4 + d6 * d6 + d7 * d7;

            // Poseidon start - configurable wrong movement flagging
            //boolean flag1 = false;
            if (worldserver.getConfig().anticheat.wrongMovementFlagging.enabled
                    && d8 > worldserver.getConfig().anticheat.wrongMovementFlagging.threshold
                    && !this.player.isSleeping()
                    && !getPlayer().hasPermission("poseidon.anticheat.wrong-movement-flagging.exempt")) {

                a.warning(this.player.name + " moved wrongly!");
                a.warning("Got position " + d1 + ", " + d2 + ", " + d3);
                a.warning("Expected " + this.player.locX + ", " + this.player.locY + ", " + this.player.locZ);
                switch (worldserver.getConfig().anticheat.wrongMovementFlagging.action) {
                    case KICK -> this.disconnect("You moved wrongly (Hacking?)");
                    case TELEPORT_BACK -> this.a(this.x, this.y, this.z, f2, f3);
                }
                return;
            }
            // Poseidon end

            this.player.setLocation(d1, d2, d3, f2, f3);
            boolean flag2 = worldserver.getEntities(this.player, this.player.boundingBox.clone().shrink(f4, f4, f4)).isEmpty();

            if (flag && !flag2 && !this.player.isSleeping()) { // Poseidon
                this.a(this.x, this.y, this.z, f2, f3);
                return;
            }

            AxisAlignedBB axisalignedbb = this.player.boundingBox.clone().b(f4, f4, f4).a(0.0D, -0.55D, 0.0D);

            if (!this.minecraftServer.allowFlight
                    && !worldserver.b(axisalignedbb)
                    && !getPlayer().hasPermission("poseidon.anticheat.flight-flagging.exempt")) { // Poseidon - add exempt permission
                if (d6 >= -0.03125D) {
                    ++this.h;
                    if (this.h > 80) {
                        a.warning(this.player.name + " was kicked for floating too long!");
                        this.disconnect("Flying is not enabled on this server");
                        return;
                    }
                }
            } else {
                this.h = 0;
            }

            this.player.onGround = packet10flying.g;
            this.minecraftServer.serverConfigurationManager.d(this.player);
            this.player.b(this.player.locY - d0, packet10flying.g);
        }
    }

    public void a(double d0, double d1, double d2, float f, float f1) {
        // CraftBukkit start - Delegate to teleport(Location)
        Player player = this.getPlayer();
        Location from = player.getLocation();
        Location to = new Location(this.getPlayer().getWorld(), d0, d1, d2, f, f1);
        PlayerTeleportEvent event = new PlayerTeleportEvent(player, from, to);
        this.server.getPluginManager().callEvent(event);

        from = event.getFrom();
        to = event.isCancelled() ? from : event.getTo();

        this.teleport(to);
    }

    public void teleport(Location dest) {
        double d0, d1, d2;
        float f, f1;

        d0 = dest.getX();
        d1 = dest.getY();
        d2 = dest.getZ();
        f = dest.getYaw();
        f1 = dest.getPitch();

        // TODO: make sure this is the best way to address this.
        if (Float.isNaN(f)) {
            f = 0;
        }

        if (Float.isNaN(f1)) {
            f1 = 0;
        }

        this.lastPosX = d0;
        this.lastPosY = d1;
        this.lastPosZ = d2;
        this.lastYaw = f;
        this.lastPitch = f1;
        this.justTeleported = true;
        // CraftBukkit end

        this.checkMovement = false;
        this.x = d0;
        this.y = d1;
        this.z = d2;
        this.player.setLocation(d0, d1, d2, f, f1);
        this.player.netServerHandler.sendPacket(new Packet13PlayerLookMove(d0, d1 + 1.6200000047683716D, d1, d2, f, f1, false));
    }

    public void a(Packet14BlockDig packet14blockdig) {
        if (this.player.dead) return; // CraftBukkit

        WorldServer worldserver = this.minecraftServer.getWorldServer(this.player.dimension);

        if (packet14blockdig.e == 4) {
            // CraftBukkit start
            // If the ticks aren't the same then the count starts from 0 and we update the lastDropTick.
            if (this.lastDropTick != MinecraftServer.currentTick) {
                this.dropCount = 0;
                this.lastDropTick = MinecraftServer.currentTick;
            } else {
                // Else we increment the drop count and check the amount.
                this.dropCount++;
                if (this.dropCount >= 20) {
                    a.warning(this.player.name + " dropped their items too quickly!");
                    this.disconnect("You dropped your items too quickly (Hacking?)");
                }
            }
            // CraftBukkit end
            this.player.F();
        } else {
            boolean flag = worldserver.weirdIsOpCache = worldserver.dimension != 0 || this.minecraftServer.serverConfigurationManager.isOp(this.player.name); // CraftBukkit
            boolean flag1 = packet14blockdig.e == 0;

            if (packet14blockdig.e == 2) {
                flag1 = true;
            }

            int i = packet14blockdig.a;
            int j = packet14blockdig.b;
            int k = packet14blockdig.c;

            if (flag1) {
                double d0 = this.player.locX - ((double) i + 0.5D);
                double d1 = this.player.locY - ((double) j + 0.5D);
                double d2 = this.player.locZ - ((double) k + 0.5D);
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;

                if (d3 > 36.0D) {
                    return;
                }
            }

            ChunkCoordinates chunkcoordinates = worldserver.getSpawn();
            int l = (int) MathHelper.abs((float) (i - chunkcoordinates.x));
            int i1 = (int) MathHelper.abs((float) (k - chunkcoordinates.z));

            if (l > i1) {
                i1 = l;
            }

            if (packet14blockdig.e == 0) {
                // CraftBukkit
                if (i1 < this.server.getSpawnRadius() && !flag) {
                    this.player.netServerHandler.sendPacket(new Packet53BlockChange(i, j, k, worldserver));
                } else {
                    // CraftBukkit - add face argument
                    this.player.itemInWorldManager.dig(i, j, k, packet14blockdig.face);
                }
            } else if (packet14blockdig.e == 2) {
                this.player.itemInWorldManager.a(i, j, k);
                if (worldserver.getTypeId(i, j, k) != 0) {
                    this.player.netServerHandler.sendPacket(new Packet53BlockChange(i, j, k, worldserver));
                }
            } else if (packet14blockdig.e == 3) {
                double d4 = this.player.locX - ((double) i + 0.5D);
                double d5 = this.player.locY - ((double) j + 0.5D);
                double d6 = this.player.locZ - ((double) k + 0.5D);
                double d7 = d4 * d4 + d5 * d5 + d6 * d6;

                if (d7 < 256.0D) {
                    this.player.netServerHandler.sendPacket(new Packet53BlockChange(i, j, k, worldserver));
                }
            }

            worldserver.weirdIsOpCache = false;
        }
    }

    public void a(Packet15Place packet15place) {
        WorldServer worldserver = this.minecraftServer.getWorldServer(this.player.dimension);

        // CraftBukkit start
        if (this.player.dead) return;

        // This is a horrible hack needed because the client sends 2 packets on 'right mouse click'
        // aimed at a block. We shouldn't need to get the second packet if the data is handled
        // but we cannot know what the client will do, so we might still get it
        //
        // If the time between packets is small enough, and the 'signature' similar, we discard the
        // second one. This sadly has to remain until Mojang makes their packets saner. :(
        //  -- Grum

        if (packet15place.face == 255) {
            if (packet15place.itemstack != null && packet15place.itemstack.id == this.lastMaterial && this.lastPacket != null && packet15place.timestamp - this.lastPacket < 100) {
                this.lastPacket = null;
                return;
            }
        } else {
            this.lastMaterial = packet15place.itemstack == null ? -1 : packet15place.itemstack.id;
            this.lastPacket = packet15place.timestamp;
        }

        // CraftBukkit - if rightclick decremented the item, always send the update packet.
        // this is not here for CraftBukkit's own functionality; rather it is to fix
        // a notch bug where the item doesn't update correctly.
        boolean always = false;

        // CraftBukkit end

        ItemStack itemstack = this.player.inventory.getItemInHand();
        boolean flag = worldserver.weirdIsOpCache = worldserver.dimension != 0 || this.minecraftServer.serverConfigurationManager.isOp(this.player.name); // CraftBukkit

        if (packet15place.face == 255) {
            if (itemstack == null) {
                return;
            }

            // CraftBukkit start
            int itemstackAmount = itemstack.count;
            PlayerInteractEvent event = CraftEventFactory.callPlayerInteractEvent(this.player, Action.RIGHT_CLICK_AIR, itemstack);
            if (event.useItemInHand() != Event.Result.DENY) {
                this.player.itemInWorldManager.useItem(this.player, this.player.world, itemstack);
            }

            // CraftBukkit - notch decrements the counter by 1 in the above method with food,
            // snowballs and so forth, but he does it in a place that doesn't cause the
            // inventory update packet to get sent
            always = (itemstack.count != itemstackAmount);
            // CraftBukkit end
        } else {
            int i = packet15place.a;
            int j = packet15place.b;
            int k = packet15place.c;
            int l = packet15place.face;
            ChunkCoordinates chunkcoordinates = worldserver.getSpawn();
            int i1 = (int) MathHelper.abs((float) (i - chunkcoordinates.x));
            int j1 = (int) MathHelper.abs((float) (k - chunkcoordinates.z));

            if (i1 > j1) {
                j1 = i1;
            }

            // CraftBukkit start - Check if we can actually do something over this large a distance
            Location eyeLoc = this.getPlayer().getEyeLocation();
            if (Math.pow(eyeLoc.getX() - i, 2) + Math.pow(eyeLoc.getY() - j, 2) + Math.pow(eyeLoc.getZ() - k, 2) > PLACE_DISTANCE_SQUARED) {
                return;
            }
            flag = true; // spawn protection moved to ItemBlock!!!
            // CraftBukkit end

            if (j1 > 16 || flag) {
                this.player.itemInWorldManager.interact(this.player, worldserver, itemstack, i, j, k, l);
            }

            this.player.netServerHandler.sendPacket(new Packet53BlockChange(i, j, k, worldserver));
            if (l == 0) {
                --j;
            }

            if (l == 1) {
                ++j;
            }

            if (l == 2) {
                --k;
            }

            if (l == 3) {
                ++k;
            }

            if (l == 4) {
                --i;
            }

            if (l == 5) {
                ++i;
            }

            this.player.netServerHandler.sendPacket(new Packet53BlockChange(i, j, k, worldserver));
        }

        itemstack = this.player.inventory.getItemInHand();
        if (itemstack != null && itemstack.count == 0) {
            this.player.inventory.items[this.player.inventory.itemInHandIndex] = null;
        }

        this.player.h = true;
        this.player.inventory.items[this.player.inventory.itemInHandIndex] = ItemStack.b(this.player.inventory.items[this.player.inventory.itemInHandIndex]);
        Slot slot = this.player.activeContainer.a(this.player.inventory, this.player.inventory.itemInHandIndex);

        this.player.activeContainer.a();
        this.player.h = false;
        // CraftBukkit
        if (!ItemStack.equals(this.player.inventory.getItemInHand(), packet15place.itemstack) || always) {
            this.sendPacket(new Packet103SetSlot(this.player.activeContainer.windowId, slot.a, this.player.inventory.getItemInHand()));
        }

        worldserver.weirdIsOpCache = false;
    }

    public void a(String s, Object @Nullable [] aobject) {
        // Poseidon start - fix disconnect spam
        if (!this.disconnected.compareAndSet(false, true)) {
            return;
        }
        // Poseidon end

        a.info(this.player.name + " lost connection: " + s);
        // CraftBukkit start - we need to handle custom quit messages
        String quitMessage = this.minecraftServer.serverConfigurationManager.disconnect(this.player);
        if (quitMessage != null) {
            this.minecraftServer.serverConfigurationManager.sendAll(new Packet3Chat(quitMessage));
        }
        // CraftBukkit end
        //this.disconnected = true; // Poseidon
    }

    public void a(Packet packet) {
        a.warning(this.getClass() + " wasn't prepared to deal with a " + packet.getClass());
        this.disconnect("Protocol error, unexpected packet");
    }

    public void sendPacket(Packet packet) {
        // Poseidon start
        if (packet == null) {
            return;
        }
        // Poseidon end

        // CraftBukkit start
        if (packet instanceof Packet6SpawnPosition packet6) {
            this.player.compassTarget = new Location(this.getPlayer().getWorld(), packet6.x, packet6.y, packet6.z);
        } else if (packet instanceof Packet3Chat packet3) {
            String message = packet3.message;
            for (final String line: TextWrapper.wrapText(message)) {
                this.networkManager.queue(new Packet3Chat(line));
            }
            packet = null;
        } // Poseidon start - handle chunk compression when writing packet
        /*else if (packet.k) {
            // Reroute all low-priority packets through to compression thread.
            ChunkCompressionThread.sendPacket(this.player, packet);
            packet = null;
        }*/
        // Poseidon end
        if (packet instanceof OutboundPacket outbound) this.networkManager.queue(outbound); // Poseidon - only queue outbound packets
        // CraftBukkit end

        this.g = this.f;
    }

    public void a(Packet16BlockItemSwitch packet16blockitemswitch) {
        if (this.player.dead) return; // CraftBukkit

        if (packet16blockitemswitch.itemInHandIndex >= 0 && packet16blockitemswitch.itemInHandIndex <= InventoryPlayer.e()) {
            // CraftBukkit start
            PlayerItemHeldEvent event = new PlayerItemHeldEvent(this.getPlayer(), this.player.inventory.itemInHandIndex, packet16blockitemswitch.itemInHandIndex);
            this.server.getPluginManager().callEvent(event);
            // CraftBukkit end

            this.player.inventory.itemInHandIndex = packet16blockitemswitch.itemInHandIndex;
        } else {
            a.warning(this.player.name + " tried to set an invalid carried item");
        }
    }

    public void a(Packet3Chat packet3chat) {
        String s = packet3chat.message;

        if (s.length() > 100) {
            this.disconnect("Chat message too long");
        } else {
            s = s.trim();

            for (int i = 0; i < s.length(); ++i) {
                if (TextWrapper.allowedChars.indexOf(s.charAt(i)) < 0) { // Poseidon
                    this.disconnect("Illegal characters in chat");
                    return;
                }
            }

            // CraftBukkit start
            this.chat(s);
        }
    }

    public boolean chat(String s) {
        if (!this.player.dead) {
            if (s.startsWith("/")) {
                this.handleCommand(s);
                return true;
            } else {
                Player player = this.getPlayer();
                PlayerChatEvent event = new PlayerChatEvent(player, s);
                this.server.getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return true;
                }

                s = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());
                minecraftServer.console.sendMessage(s);
                for (Player recipient : event.getRecipients()) {
                    recipient.sendMessage(s);
                }
            }
        }

        return false;
        // CraftBukkit end
    }

    private void handleCommand(String s) {
        // CraftBukkit start
        CraftPlayer player = this.getPlayer();

        PlayerCommandPreprocessEvent event = new PlayerCommandPreprocessEvent(player, s);
        this.server.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        try {
            // Poseidon start - log commands
            String commandString = event.getMessage().substring(1);
            boolean redact = Poseidon.getConfig().logging.redactedCommands.stream()
                    .map(regex -> Pattern.compile(regex, Pattern.CASE_INSENSITIVE))
                    .anyMatch(pattern -> pattern.matcher(commandString).find());
            String message = redact ? "COMMAND REDACTED" : event.getMessage();
            a.info(event.getPlayer().getName() + " issued server command: " + message);
            // Poseidon end
            this.server.dispatchCommand(event.getPlayer(), commandString); // Poseidon - use player and message from event
        } catch (CommandException ex) {
            player.sendMessage(ChatColor.RED + "An internal error occurred while attempting to perform this command");
            Logger.getLogger(NetServerHandler.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        // CraftBukkit end

        /* CraftBukkit start - No longer neaded av we have already handled it server.dispatchCommand above.
        if (s.toLowerCase().startsWith("/me ")) {
            s = "* " + this.player.name + " " + s.substring(s.indexOf(" ")).trim();
            a.info(s);
            this.minecraftServer.serverConfigurationManager.sendAll(new Packet3Chat(s));
        } else if (s.toLowerCase().startsWith("/kill")) {
            this.player.damageEntity(this.player, 1000); // CraftBukkit - replace null entity with player entity; TODO: decide if we want damage with a null source to fire an event.
        } else if (s.toLowerCase().startsWith("/tell ")) {
            String[] astring = s.split(" ");

            if (astring.length >= 3) {
                s = s.substring(s.indexOf(" ")).trim();
                s = s.substring(s.indexOf(" ")).trim();
                s = "\u00A77" + this.player.name + " whispers " + s;
                a.info(s + " to " + astring[1]);
                if (!this.minecraftServer.serverConfigurationManager.a(astring[1], (Packet) (new Packet3Chat(s)))) {
                    this.sendPacket(new Packet3Chat("\u00A7cThere\'s no player by that name online."));
                }
            }
        } else {
            String s1;

            if (this.minecraftServer.serverConfigurationManager.isOp(this.player.name)) {
                s1 = s.substring(1);
                a.info(this.player.name + " issued server command: " + s1);
                this.minecraftServer.issueCommand(s1, this);
            } else {
                s1 = s.substring(1);
                a.info(this.player.name + " tried command: " + s1);
            }
        }
        // CraftBukkit end */
    }

    public void a(Packet18ArmAnimation packet18armanimation) {
        if (this.player.dead) return; // CraftBukkit

        if (packet18armanimation.b == 1) {
            // CraftBukkit start - raytrace to look for 'rogue armswings'
            // Poseidon start - simplify
            Vec3D origin = this.player.getEyeLocation();
            Vec3D direction = this.player.getLookDirection();
            double distance = 5.0D;
            Vec3D destination = origin.add(direction.a * distance, direction.b * distance, direction.c * distance);
            MovingObjectPosition movingobjectposition = this.player.world.rayTrace(origin, destination, true);
            // Poseidon end
            if (movingobjectposition == null || movingobjectposition.type != EnumMovingObjectType.TILE) {
                CraftEventFactory.callPlayerInteractEvent(this.player, Action.LEFT_CLICK_AIR, this.player.inventory.getItemInHand());
            }

            // Arm swing animation
            PlayerAnimationEvent event = new PlayerAnimationEvent(this.getPlayer());
            this.server.getPluginManager().callEvent(event);

            if (event.isCancelled()) return;
            // CraftBukkit end

            this.player.w();
        }
    }

    public void a(Packet19EntityAction packet19entityaction) {
        // CraftBukkit start
        if (this.player.dead) return;

        if (packet19entityaction.animation == 1 || packet19entityaction.animation == 2) {
            PlayerToggleSneakEvent event = new PlayerToggleSneakEvent(this.getPlayer(), packet19entityaction.animation == 1);
            this.server.getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return;
            }
        }
        // CraftBukkit end

        if (packet19entityaction.animation == 1) {
            this.player.setSneak(true);
        } else if (packet19entityaction.animation == 2) {
            this.player.setSneak(false);
        } else if (packet19entityaction.animation == 3) {
            this.player.a(false, true, true);
            this.checkMovement = false;
        }
    }

    public void a(Packet255KickDisconnect packet255kickdisconnect) {
        this.networkManager.a("disconnect.quitting");
    }

    public int b() {
        return this.networkManager.e();
    }

    public void sendMessage(String s) {
        this.sendPacket(new Packet3Chat("\u00A77" + s));
    }

    public String getName() {
        return this.player.name;
    }

    public void a(Packet7UseEntity packet7useentity) {
        if (this.player.dead) return; // CraftBukkit

        WorldServer worldserver = this.minecraftServer.getWorldServer(this.player.dimension);
        Entity entity = worldserver.getEntity(packet7useentity.target);
        ItemStack itemInHand = this.player.inventory.getItemInHand();

        // Poseidon start - raytrace to check if entity is within reach and line of sight
        if (entity != null) {
            MovingObjectPosition movingobjectposition = this.player.rayTrace(3.0);
            if (movingobjectposition != null && movingobjectposition.entity == entity) {
            // Poseidon end
                if (packet7useentity.c == 0) {
                    // Poseidon start - fix minecart dupe
                    Player player = this.getPlayer();
                    org.bukkit.entity.Entity interacted = entity.getBukkitEntity();
                    if (player.isInsideVehicle() && interacted instanceof StorageMinecart) {
                        return;
                    }
                    // Poseidon end

                    // CraftBukkit start
                    PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(this.getPlayer(), entity.getBukkitEntity());
                    this.server.getPluginManager().callEvent(event);

                    if (event.isCancelled()) {
                        return;
                    }
                    // CraftBukkit end
                    this.player.c(entity);
                    // CraftBukkit start - update the client if the item is an infinite one
                    if (itemInHand != null && itemInHand.count <= -1) {
                        this.player.updateInventory(this.player.activeContainer);
                    }
                    // CraftBukkit end
                } else if (packet7useentity.c == 1) {
                    this.player.d(entity);
                    // CraftBukkit start - update the client if the item is an infinite one
                    if (itemInHand != null && itemInHand.count <= -1) {
                        this.player.updateInventory(this.player.activeContainer);
                    }
                    // CraftBukkit end
                }
            }
        }
    }

    public void a(Packet9Respawn packet9respawn) {
        if (this.player.health <= 0) {
            this.player = this.minecraftServer.serverConfigurationManager.moveToWorld(this.player, 0);

            this.getPlayer().setHandle(this.player); // CraftBukkit
        }
    }

    public void a(Packet101CloseWindow packet101closewindow) {
        if (this.player.dead) return; // CraftBukkit

        this.player.A();
    }

    public void a(Packet102WindowClick packet102windowclick) {
        if (this.player.dead) return; // CraftBukkit

        // Poseidon - check if player can use container
        if (this.player.activeContainer.windowId == packet102windowclick.a && this.player.activeContainer.c(this.player) && this.player.activeContainer.b(this.player)) {
            ItemStack itemstack = this.player.activeContainer.a(packet102windowclick.b, packet102windowclick.c, packet102windowclick.f, this.player);

            if (ItemStack.equals(packet102windowclick.e, itemstack)) {
                this.player.netServerHandler.sendPacket(new Packet106Transaction(packet102windowclick.a, packet102windowclick.d, true));
                this.player.h = true;
                this.player.activeContainer.a();
                this.player.z();
                this.player.h = false;
            } else {
                this.n.put(this.player.activeContainer.windowId, packet102windowclick.d);
                this.player.netServerHandler.sendPacket(new Packet106Transaction(packet102windowclick.a, packet102windowclick.d, false));
                this.player.activeContainer.a(this.player, false);
                ArrayList<@Nullable ItemStack> arraylist = new ArrayList<>();

                for (int i = 0; i < this.player.activeContainer.e.size(); ++i) {
                    arraylist.add(this.player.activeContainer.e.get(i).getItem());
                }

                this.player.a(this.player.activeContainer, arraylist);
            }
        }
    }

    public void a(Packet106Transaction packet106transaction) {
        if (this.player.dead) return; // CraftBukkit

        Short oshort = this.n.get(this.player.activeContainer.windowId);

        if (oshort != null && packet106transaction.b == oshort && this.player.activeContainer.windowId == packet106transaction.a && !this.player.activeContainer.c(this.player)) {
            this.player.activeContainer.a(this.player, true);
        }
    }

    public void a(Packet130UpdateSign packet130updatesign) {
        if (this.player.dead) return; // CraftBukkit

        WorldServer worldserver = this.minecraftServer.getWorldServer(this.player.dimension);

        if (worldserver.isLoaded(packet130updatesign.x, packet130updatesign.y, packet130updatesign.z)) {
            TileEntity tileentity = worldserver.getTileEntity(packet130updatesign.x, packet130updatesign.y, packet130updatesign.z);

            if (tileentity instanceof TileEntitySign tileentitysign) {
                if (!tileentitysign.a()) {
                    this.minecraftServer.c("Player " + this.player.name + " just tried to change non-editable sign");
                    // CraftBukkit
                    this.sendPacket(new Packet130UpdateSign(packet130updatesign.x, packet130updatesign.y, packet130updatesign.z, tileentitysign.lines));
                    return;
                }

                // Poseidon start - check if player editing sign is the same player who placed the sign
                if (!tileentitysign.isEditableBy(this.player)) {
                    this.minecraftServer.c("Player " + this.player.name + " just tried to change a sign they are not editing");
                    this.sendPacket(new Packet130UpdateSign(packet130updatesign.x, packet130updatesign.y, packet130updatesign.z, tileentitysign.lines));
                    return;
                }
                // Poseidon end
            }

            int i;
            int j;

            for (j = 0; j < 4; ++j) {
                boolean flag = true;

                if (packet130updatesign.lines[j].length() > 15) {
                    flag = false;
                } else {
                    for (i = 0; i < packet130updatesign.lines[j].length(); ++i) {
                        if (TextWrapper.allowedChars.indexOf(packet130updatesign.lines[j].charAt(i)) < 0) { // Poseidon
                            flag = false;
                        }
                    }
                }

                if (!flag) {
                    packet130updatesign.lines[j] = "!?";
                }
            }

            if (tileentity instanceof TileEntitySign tileentitysign1) {
                j = packet130updatesign.x;
                int k = packet130updatesign.y;

                i = packet130updatesign.z;

                // CraftBukkit start
                Player player = this.server.getPlayer(this.player);
                SignChangeEvent event = new SignChangeEvent(player.getWorld().getBlockAt(j, k, i), this.server.getPlayer(this.player), packet130updatesign.lines);
                this.server.getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    for (int l = 0; l < 4; ++l) {
                        tileentitysign1.lines[l] = event.getLine(l);
                    }
                }
                tileentitysign1.a(false); // Poseidon
                // CraftBukkit end

                tileentitysign1.update();
                worldserver.notify(j, k, i);
            }
        }
    }

    public boolean c() {
        return true;
    }
}
