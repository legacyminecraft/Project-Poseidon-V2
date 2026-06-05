package net.minecraft.server;

import java.util.function.Function;

public enum EnumCreatureType {

    // Poseidon start - make mob caps configurable
    MONSTER("monster", 0, IMonster.class, w -> w.getConfig().entities.mobCaps.monsters, Material.AIR, false),
    CREATURE("creature", 1, EntityAnimal.class, w -> w.getConfig().entities.mobCaps.animals, Material.AIR, true),
    WATER_CREATURE("waterCreature", 2, EntityWaterAnimal.class, w -> w.getConfig().entities.mobCaps.waterMobs, Material.WATER, true);
    // Poseidon end

    private final Class<? extends IAnimal> d;
    private final Function<World, Integer> e; // Poseidon - int -> Function<World, Integer>
    private final Material f;
    private final boolean g;

    private static final EnumCreatureType[] h = new EnumCreatureType[] { MONSTER, CREATURE, WATER_CREATURE};

    // Poseidon - change signature
    EnumCreatureType(String s, int i, Class<? extends IAnimal> oclass, Function<World, Integer> j, Material material, boolean flag) {
        this.d = oclass;
        this.e = j;
        this.f = material;
        this.g = flag;
    }

    public Class<? extends IAnimal> a() {
        return this.d;
    }

    // Poseidon start - make mob caps configurable
    public int getMobCap(World world) {
        return this.e.apply(world);
    }
    // Poseidon end

    public Material c() {
        return this.f;
    }

    public boolean d() {
        return this.g;
    }
}
