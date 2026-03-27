package org.bukkit.entity;

import org.jspecify.annotations.Nullable;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum CreatureType {
    CHICKEN("Chicken"),
    COW("Cow"),
    CREEPER("Creeper"),
    GHAST("Ghast"),
    GIANT("Giant"),
    MONSTER("Monster"),
    PIG("Pig"),
    PIG_ZOMBIE("PigZombie"),
    SHEEP("Sheep"),
    SKELETON("Skeleton"),
    SLIME("Slime"),
    SPIDER("Spider"),
    SQUID("Squid"),
    ZOMBIE("Zombie"),
    WOLF("Wolf");

    private String name;

    private static final Map<String, CreatureType> mapping = new HashMap<>();

    static {
        for (CreatureType type : EnumSet.allOf(CreatureType.class)) {
            mapping.put(type.name, type);
        }
    }

    CreatureType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static @Nullable CreatureType fromName(String name) {
        return mapping.get(name);
    }
}
