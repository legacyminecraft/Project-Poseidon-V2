package net.minecraft.server;

import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class EntityTypes {

    private static Map<String, Class<? extends Entity>> a = new HashMap<>();
    private static Map<Class<? extends Entity>, String> b = new HashMap<>();
    private static Map<Integer, Class<? extends Entity>> c = new HashMap<>();
    private static Map<Class<? extends Entity>, Integer> d = new HashMap<>();

    public EntityTypes() {}

    private static void a(Class<? extends Entity> oclass, String s, int i) {
        a.put(s, oclass);
        b.put(oclass, s);
        c.put(i, oclass);
        d.put(oclass, i);
    }

    public static @Nullable Entity a(String s, World world) {
        Entity entity = null;

        try {
            Class<? extends Entity> oclass = a.get(s);

            if (oclass != null) {
                entity = oclass.getConstructor(new Class[] { World.class}).newInstance(new Object[] { world});
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return entity;
    }

    public static @Nullable Entity a(NBTTagCompound nbttagcompound, World world) {
        Entity entity = null;

        try {
            Class<? extends Entity> oclass = a.get(nbttagcompound.getString("id"));

            if (oclass != null) {
                entity = oclass.getConstructor(new Class[] { World.class}).newInstance(new Object[] { world});
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if (entity != null) {
            entity.e(nbttagcompound);
        } else {
            System.out.println("Skipping Entity with id " + nbttagcompound.getString("id"));
        }

        return entity;
    }

    public static int a(Entity entity) {
        return d.get(entity.getClass());
    }

    public static @Nullable String b(Entity entity) {
        return b.get(entity.getClass());
    }

    static {
        a(EntityArrow.class, "Arrow", 10);
        a(EntitySnowball.class, "Snowball", 11);
        a(EntityItem.class, "Item", 1);
        a(EntityPainting.class, "Painting", 9);
        a(EntityLiving.class, "Mob", 48);
        a(EntityMonster.class, "Monster", 49);
        a(EntityCreeper.class, "Creeper", 50);
        a(EntitySkeleton.class, "Skeleton", 51);
        a(EntitySpider.class, "Spider", 52);
        a(EntityGiantZombie.class, "Giant", 53);
        a(EntityZombie.class, "Zombie", 54);
        a(EntitySlime.class, "Slime", 55);
        a(EntityGhast.class, "Ghast", 56);
        a(EntityPigZombie.class, "PigZombie", 57);
        a(EntityPig.class, "Pig", 90);
        a(EntitySheep.class, "Sheep", 91);
        a(EntityCow.class, "Cow", 92);
        a(EntityChicken.class, "Chicken", 93);
        a(EntitySquid.class, "Squid", 94);
        a(EntityWolf.class, "Wolf", 95);
        a(EntityTNTPrimed.class, "PrimedTnt", 20);
        a(EntityFallingSand.class, "FallingSand", 21);
        a(EntityMinecart.class, "Minecart", 40);
        a(EntityBoat.class, "Boat", 41);
    }
}
