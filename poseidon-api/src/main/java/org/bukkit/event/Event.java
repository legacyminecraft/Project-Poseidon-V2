package org.bukkit.event;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreeperPowerEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PigZapEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.painting.PaintingBreakEvent;
import org.bukkit.event.painting.PaintingPlaceEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerInventoryEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.SpawnChangeEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.io.Serializable;

/**
 * Represents an event
 * <p>
 * All events require a static method named getHandlerList() which returns the same {@link HandlerList} as {@link #getHandlers()}.
 */
public abstract class Event implements Serializable {

    @Deprecated
    private final Type type;
    private final String name;
    private final boolean isAsync;

    protected Event() {
        this(false);
    }

    protected Event(boolean isAsync) {
        this.type = Type.FIXED_EVENT;
        this.name = getClass().getName();
        this.isAsync = isAsync;
    }

    @Deprecated
    protected Event(final Type type) {
        Preconditions.checkArgument(type != null, "type is null");
        Preconditions.checkArgument(type != Type.CUSTOM_EVENT, "use Event(String) to make custom events");
        this.type = type;
        this.name = getClass().getName();
        this.isAsync = false;
    }

    @Deprecated
    protected Event(final String name) {
        Preconditions.checkArgument(name != null, "name is null");
        this.type = Type.CUSTOM_EVENT;
        this.name = name;
        this.isAsync = false;
    }

    /**
     * Calls the event and tests if cancelled.
     *
     * @return false if event was cancelled, if cancellable. otherwise true.
     */
    public boolean callEvent() {
        Bukkit.getPluginManager().callEvent(this);
        if (this instanceof Cancellable cancellable) {
            return !cancellable.isCancelled();
        } else {
            return true;
        }
    }

    /**
     * Gets the Type of this event
     *
     * @return Event type that this object represents
     */
    @Deprecated
    public final Type getType() {
        return type;
    }

    /**
     * Gets the event's name.
     *
     * @return Name of this event
     */
    public final String getEventName() {
        return name;
    }

    /**
     * Any custom event that should not by synchronized with other events must use the specific constructor.
     * These are the caveats of using an asynchronous event:
     * <li>The event is never fired from inside code triggered by a synchronous event.
     *          Attempting to do so results in an {@link java.lang.IllegalStateException}.</li>
     * <li>However, asynchronous event handlers may fire synchronous or asynchronous events</li>
     * <li>The event may be fired multiple times simultaneously and in any order.</li>
     * <li>Any newly registered or unregistered handler is ignored after an event starts execution.</li>
     * <li>The handlers for this event may block for any length of time.</li>
     * <li>Some implementations may selectively declare a specific event use as asynchronous.
     *          This behavior should be clearly defined.
     *
     * @return false by default, true if the event fires asynchronously
     */
    public final boolean isAsynchronous() {
        return isAsync;
    }

    public HandlerList getHandlers() {
        if (type == Type.CUSTOM_EVENT) {
            TransitionalCustomEvent.getHandlerList().bake();
            return TransitionalCustomEvent.getHandlerList();
        } else {
            throw new IllegalStateException("Event must implement getHandlers()");
        }
    }

    public enum Result {

        /**
         * Deny the event.
         * Depending on the event, the action indicated by the event will either not take place or will be reverted.
         * Some actions may not be denied.
         */
        DENY,
        /**
         * Neither deny nor allow the event.
         * The server will proceed with its normal handling.
         */
        DEFAULT,
        /**
         * Allow / Force the event.
         * The action indicated by the event will take place if possible, even if the server would not normally allow the action.
         * Some actions may not be allowed.
         */
        ALLOW
    }

    /**
     * Represents an events priority in execution
     */
    @Deprecated
    public enum Priority {

        /**
         * Event call is of very low importance and should be ran first, to allow
         * other plugins to further customise the outcome
         */
        Lowest(EventPriority.LOWEST),
        /**
         * Event call is of low importance
         */
        Low(EventPriority.LOW),
        /**
         * Event call is neither important or unimportant, and may be ran normally
         */
        Normal(EventPriority.NORMAL),
        /**
         * Event call is of high importance
         */
        High(EventPriority.HIGH),
        /**
         * Event call is critical and must have the final say in what happens
         * to the event
         */
        Highest(EventPriority.HIGHEST),
        /**
         * Event is listened to purely for monitoring the outcome of an event.
         *
         * No modifications to the event should be made under this priority
         */
        Monitor(EventPriority.MONITOR);

        private final EventPriority priority;

        Priority(EventPriority priority) {
            this.priority = priority;
        }

        public EventPriority getNewPriority() {
            return this.priority;
        }
    }

    /**
     * Represents a category used by Type
     */
    @Deprecated
    public enum Category {

        /**
         * Represents Player-based events
         *
         * @see Category.LIVING_ENTITY
         */
        PLAYER,
        /**
         * Represents Entity-based events
         */
        ENTITY,
        /**
         * Represents Block-based events
         */
        BLOCK,
        /**
         * Represents LivingEntity-based events
         */
        LIVING_ENTITY,
        /**
         * Represents Weather-based events
         */
        WEATHER,
        /**
         * Represents Vehicle-based events
         */
        VEHICLE,
        /**
         * Represents World-based events
         */
        WORLD,
        /**
         * Represents Server and Plugin based events
         */
        SERVER,
        /**
         * Represents Inventory-based events
         */
        INVENTORY,
        /**
         * Represents any miscellaneous events
         */
        MISCELLANEOUS
    }

    /**
     * Provides a lookup for all core events
     *
     * @see org.bukkit.event.
     */
    @Deprecated
    public enum Type {

        /**
         * PLAYER EVENTS
         */

        /**
         * Called when a player enters the world on a server
         *
         * @see org.bukkit.event.player.PlayerJoinEvent
         */
        PLAYER_JOIN(Category.PLAYER, PlayerJoinEvent.class),
        /**
         * Called when a player is attempting to connect to the server
         *
         * @see org.bukkit.event.player.PlayerLoginEvent
         */
        PLAYER_LOGIN(Category.PLAYER, PlayerLoginEvent.class),
        /**
         * Called when a player has just been authenticated
         *
         * @see org.bukkit.event.player.PlayerPreLoginEvent
         */
        PLAYER_PRELOGIN(Category.PLAYER, PlayerPreLoginEvent.class),
        /**
         * Called when a player respawns
         *
         * @see org.bukkit.event.player.PlayerRespawnEvent
         */
        PLAYER_RESPAWN(Category.PLAYER, PlayerRespawnEvent.class),
        /**
         * Called when a player gets kicked from the server
         *
         * @see org.bukkit.event.player.PlayerKickEvent
         */
        PLAYER_KICK(Category.PLAYER, PlayerKickEvent.class),
        /**
         * Called when a player sends a chat message
         *
         * @see org.bukkit.event.player.PlayerChatEvent
         */
        PLAYER_CHAT(Category.PLAYER, PlayerChatEvent.class),
        /**
         * Called when a player uses a command (early in the command handling process)
         *
         * @see org.bukkit.event.player.PlayerCommandPreprocessEvent
         */
        PLAYER_COMMAND_PREPROCESS(Category.PLAYER, PlayerCommandPreprocessEvent.class),
        /**
         * Called when a player leaves the server
         *
         * @see org.bukkit.event.player.PlayerQuitEvent
         */
        PLAYER_QUIT(Category.PLAYER, PlayerQuitEvent.class),
        /**
         * Called when a player moves position in the world
         *
         * @see org.bukkit.event.player.PlayerMoveEvent
         */
        PLAYER_MOVE(Category.PLAYER, PlayerMoveEvent.class),
        /**
         * Called before a player gets a velocity vector sent, which will instruct him to 
         * get "pushed" into a specific direction, e.g. after an explosion
         *
         * @see org.bukkit.event.player.PlayerVelocityEvent
         */
        PLAYER_VELOCITY(Category.PLAYER, PlayerVelocityEvent.class),
        /**
         * Called when a player undergoes an animation (Arm Swing is the only animation currently supported)
         *
         * @see org.bukkit.event.player.PlayerAnimationEvent
         */
        PLAYER_ANIMATION(Category.PLAYER, PlayerAnimationEvent.class),
        /**
         * Called when a player toggles sneak mode
         *
         * @see org.bukkit.event.player.PlayerToggleSneakEvent
         */
        PLAYER_TOGGLE_SNEAK(Category.PLAYER, PlayerToggleSneakEvent.class),
        /**
         * Called when a player interacts with an object or air
         *
         * @see org.bukkit.event.player.PlayerInteractEvent
         */
        PLAYER_INTERACT(Category.PLAYER, PlayerInteractEvent.class),
        /**
         * Called when a player right clicks an entity
         *
         * @see org.bukkit.event.player.PlayerInteractEntityEvent
         */
        PLAYER_INTERACT_ENTITY(Category.PLAYER, PlayerInteractEntityEvent.class),
        /**
         * Called when a player throws an egg
         *
         * @see org.bukkit.event.player.PlayerEggThrowEvent
         */
        PLAYER_EGG_THROW(Category.PLAYER, PlayerEggThrowEvent.class),
        /**
         * Called when a player teleports from one position to another
         *
         * @see org.bukkit.event.player.PlayerTeleportEvent
         */
        PLAYER_TELEPORT(Category.PLAYER, PlayerTeleportEvent.class),
        /**
         * Called when a player completes the portaling process by standing in a portal
         *
         * @see org.bukkit.event.player.PlayerPortalEvent
         */
        PLAYER_PORTAL(Category.PLAYER, PlayerPortalEvent.class),
        /**
         * Called when a player changes their held item
         *
         * @see org.bukkit.event.player.PlayerItemHeldEvent
         */
        PLAYER_ITEM_HELD(Category.PLAYER, PlayerItemHeldEvent.class),
        /**
         * Called when a player drops an item
         *
         * @see org.bukkit.event.player.PlayerDropItemEvent
         */
        PLAYER_DROP_ITEM(Category.PLAYER, PlayerDropItemEvent.class),
        /**
         * Called when a player picks an item up off the ground
         *
         * @see org.bukkit.event.player.PlayerPickupItemEvent
         */
        PLAYER_PICKUP_ITEM(Category.PLAYER, PlayerPickupItemEvent.class),
        /**
         * Called when a player empties a bucket
         *
         * @see org.bukkit.event.player.PlayerBucketEmptyEvent
         */
        PLAYER_BUCKET_EMPTY(Category.PLAYER, PlayerBucketEmptyEvent.class),
        /**
         * Called when a player fills a bucket
         *
         * @see org.bukkit.event.player.PlayerBucketFillEvent
         */
        PLAYER_BUCKET_FILL(Category.PLAYER, PlayerBucketFillEvent.class),
        /**
         * Called when a player interacts with the inventory
         *
         * @see org.bukkit.event.player.PlayerInventoryEvent
         */
        PLAYER_INVENTORY(Category.PLAYER, PlayerInventoryEvent.class),
        /**
         * Called when a player enter a bed
         *
         * @see org.bukkit.event.player.PlayerBedEnterEvent
         */
        PLAYER_BED_ENTER(Category.PLAYER, PlayerBedEnterEvent.class),
        /**
         * Called when a player leaves a bed
         *
         * @see org.bukkit.event.player.PlayerBedLeaveEvent
         */
        PLAYER_BED_LEAVE(Category.PLAYER, PlayerBedLeaveEvent.class),
        /**
         * Called when a player is fishing
         *
         * @see org.bukkit.event.player.PlayerFishEvent
         */
        PLAYER_FISH(Category.PLAYER, PlayerFishEvent.class),

        /**
         * BLOCK EVENTS
         */

        /**
         * Called when a block is damaged (hit by a player)
         *
         * @see org.bukkit.event.block.BlockDamageEvent
         */
        BLOCK_DAMAGE(Category.BLOCK, BlockDamageEvent.class),
        /**
         * Called when a block is undergoing a universe physics
         * check on whether it can be built
         *
         * For example, cacti cannot be built on grass unless overridden here
         *
         * @see org.bukkit.event.block.BlockCanBuildEvent
         */
        BLOCK_CANBUILD(Category.BLOCK, BlockCanBuildEvent.class),
        /**
         * Called when a block of water or lava attempts to flow into another
         * block
         *
         * @see org.bukkit.event.block.BlockFromToEvent
         */
        BLOCK_FROMTO(Category.BLOCK, BlockFromToEvent.class),
        /**
         * Called when a block is being set on fire from another block, such as
         * an adjacent block of fire attempting to set fire to wood
         *
         * @see org.bukkit.event.block.BlockIgniteEvent
         */
        BLOCK_IGNITE(Category.BLOCK, BlockIgniteEvent.class),
        /**
         * Called when a block undergoes a physics check
         *
         * A physics check is commonly called when an adjacent block changes
         * type
         *
         * @see org.bukkit.event.block.BlockPhysicsEvent
         */
        BLOCK_PHYSICS(Category.BLOCK, BlockPhysicsEvent.class),
        /**
         * Called when a player is attempting to place a block
         *
         * @see org.bukkit.event.block.BlockPlaceEvent
         */
        BLOCK_PLACE(Category.BLOCK, BlockPlaceEvent.class),
        /**
         * Called when a block dispenses something
         *
         * @see org.bukkit.event.block.BlockDispenseEvent
         */
        BLOCK_DISPENSE(Category.BLOCK, BlockDispenseEvent.class),
        /**
         * Called when a block is destroyed from being burnt by fire
         *
         * @see org.bukkit.event.block.BlockBurnEvent
         */
        BLOCK_BURN(Category.BLOCK, BlockBurnEvent.class),
        /**
         * Called when leaves are decaying naturally
         *
         * @see org.bukkit.event.block.LeavesDecayEvent
         */
        LEAVES_DECAY(Category.BLOCK, LeavesDecayEvent.class),
        /**
         * Called when a sign is changed
         *
         * @see org.bukkit.event.block.SignChangeEvent
         */
        SIGN_CHANGE(Category.BLOCK, SignChangeEvent.class),
        /**
         * Called when a block changes redstone current. Only triggered on blocks
         * that are actually capable of transmitting or carrying a redstone
         * current
         *
         * @see org.bukkit.event.block.BlockRedstoneEvent
         */
        REDSTONE_CHANGE(Category.BLOCK, BlockRedstoneEvent.class),
        /**
         * Called when a block is broken by a player
         *
         * @see org.bukkit.event.block.BlockBreakEvent
         */
        BLOCK_BREAK(Category.BLOCK, BlockBreakEvent.class),
        /**
         * Called when a block is formed based on world conditions
         *
         * @see org.bukkit.event.block.BlockFormEvent
         */
        BLOCK_FORM(Category.BLOCK, BlockFormEvent.class),
        /**
         * Called when a block spreads based on world conditions
         *
         * @see org.bukkit.event.block.BlockSpreadEvent
         */
        BLOCK_SPREAD(Category.BLOCK, BlockSpreadEvent.class),
        /**
         * Called when a block fades, melts or disappears based on world conditions
         *
         * @see org.bukkit.event.block.BlockFadeEvent
         */
        BLOCK_FADE(Category.BLOCK, BlockFadeEvent.class),
        /**
         * Called when a piston extends
         *
         * @see org.bukkit.event.block.PistonExtendEvent
         */
        BLOCK_PISTON_EXTEND(Category.BLOCK, BlockPistonExtendEvent.class),
        /**
         * Called when a piston retracts
         *
         * @see org.bukkit.event.block.PistonRetractEvent
         */
        BLOCK_PISTON_RETRACT(Category.BLOCK, BlockPistonRetractEvent.class),

        /**
         * INVENTORY EVENTS
         */

        /**
         * Called when a player opens an inventory
         *
         * @todo: add javadoc see comment
         */
        INVENTORY_OPEN(Category.INVENTORY, null),
        /**
         * Called when a player closes an inventory
         *
         * @todo: add javadoc see comment
         */
        INVENTORY_CLOSE(Category.INVENTORY, null),
        /**
         * Called when a player clicks on an inventory slot
         *
         * @todo: add javadoc see comment
         */
        INVENTORY_CLICK(Category.INVENTORY, null),
        /**
         * Called when an inventory slot changes values or type
         *
         * @todo: add javadoc see comment
         */
        INVENTORY_CHANGE(Category.INVENTORY, null),
        /**
         * Called when a player is attempting to perform an inventory transaction
         *
         * @todo: add javadoc see comment
         */
        INVENTORY_TRANSACTION(Category.INVENTORY, null),
        /**
         * Called when an ItemStack is successfully smelted in a furnace.
         *
         * @see org.bukkit.event.inventory.FurnaceSmeltEvent
         */
        FURNACE_SMELT(Category.INVENTORY, FurnaceSmeltEvent.class),
        /**
         * Called when an ItemStack is successfully burned as fuel in a furnace.
         *
         * @see org.bukkit.event.inventory.FurnaceBurnEvent
         */
        FURNACE_BURN(Category.INVENTORY, FurnaceBurnEvent.class),

        /**
         * SERVER EVENTS
         */

        /**
         * Called when a plugin is enabled
         *
         * @see org.bukkit.event.server.PluginEnableEvent
         */
        PLUGIN_ENABLE(Category.SERVER, PluginEnableEvent.class),
        /**
         * Called when a plugin is disabled
         *
         * @see org.bukkit.event.server.PluginDisableEvent
         */
        PLUGIN_DISABLE(Category.SERVER, PluginDisableEvent.class),
        /**
         * Called when a server command is called
         *
         * @see org.bukkit.event.server.ServerCommandEvent
         */
        SERVER_COMMAND(Category.SERVER, ServerCommandEvent.class),
        /**
         * Called when a map is initialized (created or loaded into memory)
         * 
         * @see org.bukkit.event.server.MapInitializeEvent
         */
        MAP_INITIALIZE(Category.SERVER, MapInitializeEvent.class),

        /**
         * WORLD EVENTS
         */

        /**
         * Called when a chunk is loaded
         *
         * If a new chunk is being generated for loading, it will call
         * Type.CHUNK_GENERATION and then Type.CHUNK_LOADED upon completion
         *
         * @see org.bukkit.event.world.ChunkLoadEvent
         */
        CHUNK_LOAD(Category.WORLD, ChunkLoadEvent.class),
        /**
         * Called when a chunk is unloaded
         *
         * @see org.bukkit.event.world.ChunkUnloadEvent
         */
        CHUNK_UNLOAD(Category.WORLD, ChunkUnloadEvent.class),
        /**
         * Called when a newly created chunk has been populated.
         *
         * If your intent is to populate the chunk using this event, please see {@link BlockPopulator}
         *
         * @see org.bukkit.event.world.ChunkPopulateEvent
         */
        CHUNK_POPULATED(Category.WORLD, ChunkPopulateEvent.class),
        /**
         * Called when an ItemEntity spawns in the world
         *
         * @see org.bukkit.event.entity.ItemSpawnEvent
         */
        ITEM_SPAWN(Category.WORLD, ItemSpawnEvent.class),
        /**
         * Called when a World's spawn is changed
         *
         * @see org.bukkit.event.world.SpawnChangeEvent
         */
        SPAWN_CHANGE(Category.WORLD, SpawnChangeEvent.class),
        /**
         * Called when a world is saved
         *
         * @see org.bukkit.event.world.WorldSaveEvent
         */
        WORLD_SAVE(Category.WORLD, WorldSaveEvent.class),
        /**
         * Called when a World is initializing
         *
         * @see org.bukkit.event.world.WorldInitEvent
         */
        WORLD_INIT(Category.WORLD, WorldInitEvent.class),
        /**
         * Called when a World is loaded
         *
         * @see org.bukkit.event.world.WorldLoadEvent
         */
        WORLD_LOAD(Category.WORLD, WorldLoadEvent.class),
        /**
         * Called when a World is unloaded
         *
         * @see org.bukkit.event.world.WorldUnloadEvent
         */
        WORLD_UNLOAD(Category.WORLD, WorldUnloadEvent.class),
        /**
         * Called when world attempts to create a matching end to a portal
         *
         * @see org.bukkit.event.world.PortalCreateEvent
         */
        PORTAL_CREATE(Category.WORLD, PortalCreateEvent.class),

        /**
         * ENTITY EVENTS
         */

        /**
         * Called when a painting is placed by player
         *
         * @see org.bukkit.event.painting.PaintingPlaceEvent
         */
        PAINTING_PLACE(Category.ENTITY, PaintingPlaceEvent.class),
        /**
         * Called when a painting is removed
         *
         * @see org.bukkit.event.painting.PaintingBreakEvent
         */
        PAINTING_BREAK(Category.ENTITY, PaintingBreakEvent.class),
        /**
         * Called when an entity touches a portal block
         *
         * @see org.bukkit.event.entity.EntityPortalEnterEvent
         */
        ENTITY_PORTAL_ENTER(Category.ENTITY, EntityPortalEnterEvent.class),

        /**
         * LIVING_ENTITY EVENTS
         */

        /**
         * Called when a creature, either hostile or neutral, attempts to spawn
         * in the world "naturally"
         *
         * @see org.bukkit.event.entity.CreatureSpawnEvent
         */
        CREATURE_SPAWN(Category.LIVING_ENTITY, CreatureSpawnEvent.class),
        /**
         * Called when a LivingEntity is damaged with no source.
         *
         * @see org.bukkit.event.entity.EntityDamageEvent
         */
        ENTITY_DAMAGE(Category.LIVING_ENTITY, EntityDamageEvent.class),
        /**
         * Called when a LivingEntity dies
         *
         * @see org.bukkit.event.entity.EntityDeathEvent
         */
        ENTITY_DEATH(Category.LIVING_ENTITY, EntityDeathEvent.class),
        /**
         * Called when a Skeleton or Zombie catch fire due to the sun
         *
         * @see org.bukkit.event.entity.EntityCombustEvent
         */
        ENTITY_COMBUST(Category.LIVING_ENTITY, EntityCombustEvent.class),
        /**
         * Called when an entity explodes, either TNT, Creeper, or Ghast Fireball
         *
         * @see org.bukkit.event.entity.EntityExplodeEvent
         */
        ENTITY_EXPLODE(Category.LIVING_ENTITY, EntityExplodeEvent.class),
        /**
         * Called when an entity has made a decision to explode.
         *
         * Provides an opportunity to act on the entity, change the explosion radius,
         * or to change the fire-spread flag.
         *
         * Canceling the event negates the entity's decision to explode.
         * For EntityCreeper, this resets the fuse but does not kill the Entity.
         * For EntityFireball and EntityTNTPrimed....?
         *
         * @see org.bukkit.event.entity.ExplosionPrimeEvent
         */
        EXPLOSION_PRIME(Category.LIVING_ENTITY, ExplosionPrimeEvent.class),
        /**
         * Called when an entity targets another entity
         *
         * @see org.bukkit.event.entity.EntityTargetEvent
         */
        ENTITY_TARGET(Category.LIVING_ENTITY, EntityTargetEvent.class),
        /**
         * Called when an entity interacts with a block
         * This event specifically excludes player entities
         *
         * @see org.bukkit.event.entity.EntityInteractEvent
         */
        ENTITY_INTERACT(Category.LIVING_ENTITY, EntityInteractEvent.class),
        /**
         * Called when a creeper gains or loses a power shell
         *
         * @see org.bukkit.event.entity.CreeperPowerEvent
         */
        CREEPER_POWER(Category.LIVING_ENTITY, CreeperPowerEvent.class),
        /**
         * Called when a pig is zapped, zombifying it
         *
         * @see org.bukkit.event.entity.PigZapEvent
         */
        PIG_ZAP(Category.LIVING_ENTITY, PigZapEvent.class),
        /**
         * Called when a LivingEntity is tamed
         *
         * @see org.bukkit.event.entity.EntityTameEvent
         */
        ENTITY_TAME(Category.LIVING_ENTITY, EntityTameEvent.class),
        /**
         * Called when a Projectile hits something
         *
         * @see org.bukkit.event.entity.ProjectileHitEvent
         */
        PROJECTILE_HIT(Category.ENTITY, ProjectileHitEvent.class),

        /**
         * Called when a LivingEntity is regains health
         *
         * @see org.bukkit.event.entity.EntityRegainHealthEvent
         */
        ENTITY_REGAIN_HEALTH(Category.LIVING_ENTITY, EntityRegainHealthEvent.class),

        /**
         * WEATHER EVENTS
         */

        /**
         * Called when a lightning entity strikes somewhere
         *
         * @see org.bukkit.event.weather.LightningStrikeEvent
         */
        LIGHTNING_STRIKE(Category.WEATHER, LightningStrikeEvent.class),
        /**
         * Called when the weather in a world changes
         *
         * @see org.bukkit.event.weather.WeatherChangeEvent
         */
        WEATHER_CHANGE(Category.WEATHER, WeatherChangeEvent.class),
        /**
         * Called when the thunder state in a world changes
         *
         * @see org.bukkit.event.weather.ThunderChangeEvent
         */
        THUNDER_CHANGE(Category.WEATHER, ThunderChangeEvent.class),

        /**
         * VEHICLE EVENTS
         */

        /**
         * Called when a vehicle is placed by a player
         *
         * @see org.bukkit.event.vehicle.VehicleCreateEvent
         */
        VEHICLE_CREATE(Category.VEHICLE, VehicleCreateEvent.class),
        /**
         * Called when a vehicle is destroyed
         *
         * @see org.bukkit.event.vehicle.VehicleDestroyEvent
         */
        VEHICLE_DESTROY(Category.VEHICLE, VehicleDestroyEvent.class),
        /**
         * Called when a vehicle is damaged by a LivingEntity
         *
         * @see org.bukkit.event.vehicle.VehicleDamageEvent
         */
        VEHICLE_DAMAGE(Category.VEHICLE, VehicleDamageEvent.class),
        /**
         * Called when a vehicle collides with an Entity
         *
         * @see org.bukkit.event.vehicle.VehicleCollisionEvent
         */
        VEHICLE_COLLISION_ENTITY(Category.VEHICLE, VehicleEntityCollisionEvent.class),
        /**
         * Called when a vehicle collides with a Block
         *
         * @see org.bukkit.event.vehicle.VehicleBlockCollisionEvent
         */
        VEHICLE_COLLISION_BLOCK(Category.VEHICLE, VehicleBlockCollisionEvent.class),
        /**
         * Called when a vehicle is entered by a LivingEntity
         *
         * @see org.bukkit.event.vehicle.VehicleEnterEvent
         */
        VEHICLE_ENTER(Category.VEHICLE, VehicleEnterEvent.class),
        /**
         * Called when a vehicle is exited by a LivingEntity
         *
         * @see org.bukkit.event.vehicle.VehicleExitEvent
         */
        VEHICLE_EXIT(Category.VEHICLE, VehicleExitEvent.class),
        /**
         * Called when a vehicle moves position in the world
         *
         * @see org.bukkit.event.vehicle.VehicleMoveEvent
         */
        VEHICLE_MOVE(Category.VEHICLE, VehicleMoveEvent.class),
        /**
         * Called when a vehicle is going through an update cycle, rechecking itself
         *
         * @see org.bukkit.event.vehicle.VehicleUpdateEvent
         */
        VEHICLE_UPDATE(Category.VEHICLE, VehicleUpdateEvent.class),
        /**
         * MISCELLANEOUS EVENTS
         */

        /**
         * Represents a custom event, isn't actually used
         */
        CUSTOM_EVENT(Category.MISCELLANEOUS, TransitionalCustomEvent.class),
        /**
         * Represents an event using the new, Event.Type-less event system to avoid NPE-ing
         */
        FIXED_EVENT(Category.MISCELLANEOUS, Event.class);

        private final Category category;
        private final Class<? extends Event> eventClass;

        Type(Category category, Class<? extends Event> eventClass) {
            this.category = category;
            this.eventClass = eventClass;
        }

        /**
         * Gets the Category assigned to this event
         *
         * @return Category of this Event.Type
         */
        public Category getCategory() {
            return category;
        }

        public Class<? extends Event> getEventClass() {
            return eventClass;
        }
    }
}
