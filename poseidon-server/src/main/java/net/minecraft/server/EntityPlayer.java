package net.minecraft.server;

import com.legacyminecraft.poseidon.Poseidon;
import com.legacyminecraft.poseidon.event.entity.PlayerDeathEvent;
import com.legacyminecraft.poseidon.event.profile.PlayerProfileNameChangedEvent;
import com.legacyminecraft.poseidon.profile.MinecraftProfile;
import com.legacyminecraft.poseidon.profile.PlayerProfileImpl;
import com.legacyminecraft.poseidon.util.ChunkPos;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftInventoryPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.inventory.PlayerInventory;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class EntityPlayer extends EntityHuman implements ICrafting {

    public NetServerHandler netServerHandler;
    public MinecraftServer b;
    public ItemInWorldManager itemInWorldManager;
    public double d;
    public double e;
    public LongArrayList chunkCoordIntPairQueue = new LongArrayList(); // Poseidon - LinkedList -> LongArrayList
    public LongOpenHashSet playerChunkCoordIntPairs = new LongOpenHashSet(); // Poseidon - HashSet -> LongOpenHashSet
    private int bL = -99999999;
    private int bM = 60;
    private @Nullable ItemStack[] bN = new @Nullable ItemStack[] { null, null, null, null, null};
    private int bO = 0;
    public boolean h;

    // Poseidon start
    public final MinecraftProfile profile;
    public final IntArrayList removeQueue = new IntArrayList();
    // Poseidon end

    // Poseidon - change signature
    public EntityPlayer(MinecraftServer minecraftserver, World world, MinecraftProfile profile, ItemInWorldManager iteminworldmanager) {
        super(world);
        iteminworldmanager.player = this;
        this.itemInWorldManager = iteminworldmanager;
        ChunkCoordinates chunkcoordinates = world.getSpawn();
        int i = chunkcoordinates.x;
        int j = chunkcoordinates.z;
        int k = chunkcoordinates.y;

        if (!world.worldProvider.e) {
            // Poseidon start - configurable spawn randomization radius
            int radius = world.getConfig().spawnRandomizationRadius;
            i += this.random.nextInt((radius * 2) + 1) - radius;
            j += this.random.nextInt((radius * 2) + 1) - radius;
            // Poseidon end
            k = world.f(i, j);
        }

        this.setPositionRotation((double) i + 0.5D, k, (double) j + 0.5D, 0.0F, 0.0F);
        this.b = minecraftserver;
        this.bs = 0.0F;
        // Poseidon start
        this.name = profile.name();
        this.nameTag = profile.name();
        // Poseidon end
        this.height = 0.0F;

        // Poseidon start
        this.uniqueId = profile.id();
        this.profile = profile;
        // Poseidon end

        // CraftBukkit start
        this.displayName = this.name;
    }

    public String displayName;
    public org.bukkit.@Nullable Location compassTarget;
    // CraftBukkit end

    public void spawnIn(@Nullable World world) {
        super.spawnIn(world);
        // CraftBukkit - world fallback code, either respawn location or global spawn
        if (world == null) {
            this.dead = false;
            ChunkCoordinates position = null;
            if (this.spawnWorld != null && !this.spawnWorld.equals("")) {
                CraftWorld cworld = (CraftWorld) Bukkit.getServer().getWorld(this.spawnWorld);
                if (cworld != null && this.getBed() != null) {
                    world = cworld.getHandle();
                    position = EntityHuman.getBed(cworld.getHandle(), this.getBed());
                }
            }
            if (world == null || position == null) {
                world = ((CraftWorld) Bukkit.getServer().getWorlds().get(0)).getHandle();
                position = world.getSpawn();
            }
            this.world = world;
            this.setPosition(position.x + 0.5, position.y, position.z + 0.5);
        }
        this.dimension = ((WorldServer) this.world).dimension;
        // CraftBukkit end
        this.itemInWorldManager = new ItemInWorldManager((WorldServer) world);
        this.itemInWorldManager.player = this;
    }

    // Poseidon start
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        NBTTagCompound bukkitData = nbttagcompound.k("bukkit");
        String lastKnownName = bukkitData.getString("lastKnownName");
        if (lastKnownName != null && !lastKnownName.isEmpty() && !this.profile.name().equals(lastKnownName)) {
            MinecraftServer.log.info(lastKnownName + " has changed their name to " + this.profile.name() + " (UUID: " + this.profile.id() + ")");
            new PlayerProfileNameChangedEvent(new PlayerProfileImpl(this.profile), lastKnownName).callEvent();
        }
    }
    // Poseidon end

    public void syncInventory() {
        this.activeContainer.a((ICrafting) this);
    }

    public @Nullable ItemStack[] getEquipment() {
        return this.bN;
    }

    protected void s() {
        this.height = 0.0F;
    }

    public float t() {
        return 1.62F;
    }

    public void m_() {
        this.itemInWorldManager.a();
        --this.bM;
        this.activeContainer.a();

        for (int i = 0; i < 5; ++i) {
            ItemStack itemstack = this.c_(i);

            if (itemstack != this.bN[i]) {
                this.b.getTracker(this.dimension).a(this, new Packet5EntityEquipment(this.id, i, itemstack));
                this.bN[i] = itemstack;
            }
        }
    }

    public @Nullable ItemStack c_(int i) {
        return i == 0 ? this.inventory.getItemInHand() : this.inventory.armor[i - 1];
    }

    public void die(@Nullable Entity entity) {
        // CraftBukkit start
        java.util.List<org.bukkit.inventory.ItemStack> loot = new java.util.ArrayList<>();

        for (int i = 0; i < this.inventory.items.length; ++i) {
            if (this.inventory.items[i] != null) {
                loot.add(new CraftItemStack(this.inventory.items[i]));
            }
        }

        for (int i = 0; i < this.inventory.armor.length; ++i) {
            if (this.inventory.armor[i] != null) {
                loot.add(new CraftItemStack(this.inventory.armor[i]));
            }
        }

        Player bukkitEntity = (Player) this.getBukkitEntity(); // Poseidon - cast to Player
        CraftWorld bworld = this.world.getWorld();
        PlayerInventory inventoryToKeep = new CraftInventoryPlayer(new InventoryPlayer(null)); // Poseidon

        PlayerDeathEvent event = new PlayerDeathEvent(bukkitEntity, loot, inventoryToKeep); // Poseidon - EntityDeathEvent -> PlayerDeathEvent
        this.world.getServer().getPluginManager().callEvent(event);

        // Poseidon start
        if (event.getDeathMessage() != null && !event.getDeathMessage().isBlank()) {
            this.b.serverConfigurationManager.sendAll(new Packet3Chat(event.getDeathMessage()));
        }

        event.getPlayer().getInventory().setContents(inventoryToKeep.getContents());
        event.getPlayer().getInventory().setArmorContents(inventoryToKeep.getArmorContents());
        // Poseidon end

        for (org.bukkit.inventory.ItemStack stack : event.getDrops()) {
            bworld.dropItemNaturally(bukkitEntity.getLocation(), stack);
        }

        this.y();
        // CraftBukkit end

        // Poseidon start - fix player death animation
        if (this.world.getConfig().entities.fixPlayerDeathAnimation) {
            for (int i = 0; i < 5; ++i) {
                ItemStack itemstack = this.bN[i];
                if (itemstack != null && itemstack.id > 0) {
                    this.b.getTracker(this.dimension).a(this, new Packet5EntityEquipment(this.id, i, null));
                }
            }
            this.b.getTracker(this.dimension).a(this, new Packet38EntityStatus(this.id, (byte) 3));
        }
        // Poseidon end

        updateInventory(this.activeContainer); // Poseidon - update client-side inventory on death
    }

    public boolean damageEntity(@Nullable Entity entity, int i) {
        if (this.bM > 0) {
            return false;
        } else {
            // CraftBukkit - this.b.pvpMode -> this.world.pvpMode
            if (!this.world.pvpMode) {
                if (entity instanceof EntityHuman) {
                    return false;
                }

                if (entity instanceof EntityArrow entityarrow) {
                    if (entityarrow.shooter instanceof EntityHuman) {
                        return false;
                    }
                }
            }

            return super.damageEntity(entity, i);
        }
    }

    protected boolean j_() {
        return this.b.pvpMode;
    }

    public void b(int i) {
        super.b(i, RegainReason.EATING);
    }

    // Poseidon start
    public WorldServer getWorldServer() {
        return (WorldServer) this.world;
    }
    // Poseidon end

    public void a(boolean flag) {
        super.m_();

        // Poseidon start
        int length = Math.min(this.removeQueue.size(), 127);
        for (int i = 0; i < length; i++) {
            int entityId = this.removeQueue.getInt(i);
            this.netServerHandler.sendPacket(new Packet29DestroyEntity(entityId));
        }
        this.removeQueue.removeElements(0, length);
        // Poseidon end

        for (int i = 0; i < this.inventory.getSize(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);

            if (itemstack != null && Item.byId[itemstack.id].b() && this.netServerHandler.b() <= 2) {
                Packet packet = ((ItemWorldMapBase) Item.byId[itemstack.id]).b(itemstack, this.world, this);

                if (packet != null) {
                    this.netServerHandler.sendPacket(packet);
                }
            }
        }

        if (flag && !this.chunkCoordIntPairQueue.isEmpty()) {
            // Poseidon start - improve chunk sending
            WorldServer worldserver = this.getWorldServer();
            int maxChunkPacketsPerTick = Poseidon.getConfig().network.maxChunkPacketsPerTick;
            LongArrayList chunksToRequeue = null;

            for (int count = 0; !this.chunkCoordIntPairQueue.isEmpty() && count < maxChunkPacketsPerTick; count++) {
                long chunkPos = this.chunkCoordIntPairQueue.removeLong(0);
                Chunk chunk = worldserver.chunkProviderServer.getChunkAt(ChunkPos.x(chunkPos), ChunkPos.z(chunkPos));

                if (!chunk.done
                        || (worldserver.antiXrayEngine.isEnabled()
                        && !worldserver.antiXrayEngine.areAdjacentChunksLoaded(chunk.x, chunk.z))) {
                    if (chunksToRequeue == null) {
                        chunksToRequeue = new LongArrayList();
                    }
                    chunksToRequeue.add(chunkPos);
                    count--;
                    continue;
                }

                boolean obfuscate = !((Player) this.getBukkitEntity()).hasPermission("poseidon.anticheat.anti-xray.exempt");
                this.netServerHandler.sendPacket(new Packet51MapChunk(chunk, obfuscate));
                worldserver.tracker.a(this, chunk);
                chunk.tileEntities.values().forEach(this::a);
            }

            if (chunksToRequeue != null) {
                this.chunkCoordIntPairQueue.addAll(chunksToRequeue);
            }
            // Poseidon end
        }

        if (this.E) {
            //if (this.b.propertyManager.getBoolean("allow-nether", true)) { // CraftBukkit
                if (this.activeContainer != this.defaultContainer) {
                    this.y();
                }

                if (this.vehicle != null) {
                    this.mount(this.vehicle);
                } else {
                    this.F += 0.0125F;
                    if (this.F >= 1.0F) {
                        this.F = 1.0F;
                        this.D = 10;
                        this.b.serverConfigurationManager.f(this);
                    }
                }

                this.E = false;
            //} // CraftBukkit
        } else {
            if (this.F > 0.0F) {
                this.F -= 0.05F;
            }

            if (this.F < 0.0F) {
                this.F = 0.0F;
            }
        }

        if (this.D > 0) {
            --this.D;
        }

        if (this.health != this.bL) {
            this.netServerHandler.sendPacket(new Packet8UpdateHealth(this.health));
            this.bL = this.health;
        }
    }

    private void a(TileEntity tileentity) {
        if (tileentity != null) {
            Packet packet = tileentity.f();

            if (packet != null) {
                this.netServerHandler.sendPacket(packet);
            }
        }
    }

    public void v() {
        super.v();
    }

    public void receive(Entity entity, int i) {
        if (!entity.dead) {
            EntityTracker entitytracker = this.b.getTracker(this.dimension);

            if (entity instanceof EntityItem) {
                entitytracker.a(entity, new Packet22Collect(entity.id, this.id));
            }

            if (entity instanceof EntityArrow) {
                entitytracker.a(entity, new Packet22Collect(entity.id, this.id));
            }
        }

        super.receive(entity, i);
        this.activeContainer.a();
    }

    public void w() {
        if (!this.p) {
            this.q = -1;
            this.p = true;
            EntityTracker entitytracker = this.b.getTracker(this.dimension);

            entitytracker.a(this, new Packet18ArmAnimation(this, 1));
        }
    }

    public void x() {}

    public EnumBedError a(int i, int j, int k) {
        EnumBedError enumbederror = super.a(i, j, k);

        if (enumbederror == EnumBedError.OK) {
            EntityTracker entitytracker = this.b.getTracker(this.dimension);
            Packet17 packet17 = new Packet17(this, 0, i, j, k);

            entitytracker.a(this, packet17);
            this.netServerHandler.a(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
            this.netServerHandler.sendPacket(packet17);
        }

        return enumbederror;
    }

    public void a(boolean flag, boolean flag1, boolean flag2) {
        if (this.isSleeping()) {
            EntityTracker entitytracker = this.b.getTracker(this.dimension);

            entitytracker.sendPacketToEntity(this, new Packet18ArmAnimation(this, 3));
        }

        super.a(flag, flag1, flag2);
        if (this.netServerHandler != null) {
            this.netServerHandler.a(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
        }
    }

    public void mount(@Nullable Entity entity) {
        // CraftBukkit start
        this.setPassengerOf(entity);
    }

    public void setPassengerOf(@Nullable Entity entity) {
        // mount(null) doesn't really fly for overloaded methods,
        // so this method is needed

        super.setPassengerOf(entity);
        // CraftBukkit end

        this.netServerHandler.sendPacket(new Packet39AttachEntity(this, this.vehicle));
        this.netServerHandler.a(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
    }

    protected void a(double d0, boolean flag) {}

    public void b(double d0, boolean flag) {
        super.a(d0, flag);
    }

    private void ai() {
        this.bO = this.bO % 100 + 1;
    }

    public void b(int i, int j, int k) {
        this.ai();
        this.netServerHandler.sendPacket(new Packet100OpenWindow(this.bO, 1, "Crafting", 9));
        this.activeContainer = new ContainerWorkbench(this.inventory, this.world, i, j, k);
        this.activeContainer.windowId = this.bO;
        this.activeContainer.a((ICrafting) this);
    }

    public void a(IInventory iinventory) {
        this.ai();
        this.netServerHandler.sendPacket(new Packet100OpenWindow(this.bO, 0, iinventory.getName(), iinventory.getSize()));
        this.activeContainer = new ContainerChest(this.inventory, iinventory);
        this.activeContainer.windowId = this.bO;
        this.activeContainer.a((ICrafting) this);
    }

    public void a(TileEntityFurnace tileentityfurnace) {
        this.ai();
        this.netServerHandler.sendPacket(new Packet100OpenWindow(this.bO, 2, tileentityfurnace.getName(), tileentityfurnace.getSize()));
        this.activeContainer = new ContainerFurnace(this.inventory, tileentityfurnace);
        this.activeContainer.windowId = this.bO;
        this.activeContainer.a((ICrafting) this);
    }

    public void a(TileEntityDispenser tileentitydispenser) {
        this.ai();
        this.netServerHandler.sendPacket(new Packet100OpenWindow(this.bO, 3, tileentitydispenser.getName(), tileentitydispenser.getSize()));
        this.activeContainer = new ContainerDispenser(this.inventory, tileentitydispenser);
        this.activeContainer.windowId = this.bO;
        this.activeContainer.a((ICrafting) this);
    }

    public void a(Container container, int i, @Nullable ItemStack itemstack) {
        if (!(container.b(i) instanceof SlotResult)) {
            if (!this.h) {
                this.netServerHandler.sendPacket(new Packet103SetSlot(container.windowId, i, itemstack));
            }
        }
    }

    public void updateInventory(Container container) {
        this.a(container, container.b());
    }

    public void a(Container container, List<@Nullable ItemStack> list) {
        this.netServerHandler.sendPacket(new Packet104WindowItems(container.windowId, list));
        this.netServerHandler.sendPacket(new Packet103SetSlot(-1, -1, this.inventory.j()));
    }

    public void a(Container container, int i, int j) {
        this.netServerHandler.sendPacket(new Packet105CraftProgressBar(container.windowId, i, j));
    }

    public void a(@Nullable ItemStack itemstack) {}

    public void y() {
        this.netServerHandler.sendPacket(new Packet101CloseWindow(this.activeContainer.windowId));
        this.A();
    }

    public void z() {
        if (!this.h) {
            this.netServerHandler.sendPacket(new Packet103SetSlot(-1, -1, this.inventory.j()));
        }
    }

    public void A() {
        this.activeContainer.a((EntityHuman) this);
        this.activeContainer = this.defaultContainer;
    }

    public void a(float f, float f1, boolean flag, boolean flag1, float f2, float f3) {
        this.az = f;
        this.aA = f1;
        this.aC = flag;
        this.setSneak(flag1);
        this.pitch = f2;
        this.yaw = f3;
    }

    public void a(Statistic statistic, int i) {
        if (statistic != null) {
            if (!statistic.g) {
                while (i > 100) {
                    this.netServerHandler.sendPacket(new Packet200Statistic(statistic.e, 100));
                    i -= 100;
                }

                this.netServerHandler.sendPacket(new Packet200Statistic(statistic.e, i));
            }
        }
    }

    public void B() {
        if (this.vehicle != null) {
            this.mount(this.vehicle);
        }

        if (this.passenger != null) {
            this.passenger.mount(this);
        }

        if (this.sleeping) {
            this.a(true, false, false);
        }
    }

    public void C() {
        this.bL = -99999999;
    }

    public void a(String s) {
        StatisticStorage statisticstorage = StatisticStorage.a();
        String s1 = statisticstorage.a(s);

        this.netServerHandler.sendPacket(new Packet3Chat(s1));
    }

    // CraftBukkit start
    public long timeOffset = 0;
    public boolean relativeTime = true;

    public long getPlayerTime() {
        if (this.relativeTime) {
            // Adds timeOffset to the current server time.
            return this.world.getTime() + this.timeOffset;
        } else {
            // Adds timeOffset to the beginning of this day.
            return this.world.getTime() - (this.world.getTime() % 24000) + this.timeOffset;
        }
    }

    @Override
    public String toString() {
        return super.toString() + "(" + this.name + " at " + this.locX + "," + this.locY + "," + this.locZ + ")";
    }
    // CraftBukkit end
}
