package com.legacyminecraft.poseidon.persistence;

import com.google.common.base.Preconditions;
import net.minecraft.server.NBTBase;
import net.minecraft.server.NBTTagCompound;
import org.jspecify.annotations.Nullable;

import java.util.Set;

public class PersistentDataContainerImpl implements PersistentDataContainer {

    public static final String TAG_KEY = "PersistentDataContainer";
    private static final PersistentDataTypeRegistry REGISTRY = new PersistentDataTypeRegistry();

    private final NBTTagCompound compound;

    public PersistentDataContainerImpl() {
        this.compound = new NBTTagCompound();
    }

    public PersistentDataContainerImpl(NBTTagCompound compound) {
        this.compound = compound;
    }

    @Override
    public <P, C> void set(String key, PersistentDataType<P, C> type, C value) {
        Preconditions.checkArgument(key != null, "key cannot be null");
        Preconditions.checkArgument(type != null, "type cannot be null");
        Preconditions.checkArgument(value != null, "value cannot be null");

        NBTBase tag = REGISTRY.getOrCreateAdapter(type).serialize(type.toPrimitive(value));
        getCompound().a(key, tag);
    }

    @Override
    public void remove(String key) {
        Preconditions.checkArgument(key != null, "key cannot be null");

        getCompound().a.remove(key);
    }

    @Override
    public <P, C> boolean has(String key, PersistentDataType<P, C> type) {
        Preconditions.checkArgument(key != null, "key cannot be null");
        Preconditions.checkArgument(type != null, "type cannot be null");

        NBTBase tag = getCompound().a.get(key);
        return tag != null && REGISTRY.getOrCreateAdapter(type).matches(tag);
    }

    @Override
    public boolean has(String key) {
        Preconditions.checkArgument(key != null, "key cannot be null");

        return getCompound().a.containsKey(key);
    }

    @Override
    public <P, C> @Nullable C get(String key, PersistentDataType<P, C> type) {
        Preconditions.checkArgument(key != null, "key cannot be null");
        Preconditions.checkArgument(type != null, "type cannot be null");

        NBTBase tag = getCompound().a.get(key);
        if (tag == null || !REGISTRY.getOrCreateAdapter(type).matches(tag)) {
            return null;
        }
        P primitive = REGISTRY.getOrCreateAdapter(type).deserialize(tag);
        return type.fromPrimitive(primitive);
    }

    @Override
    public <P, C> C getOrDefault(String key, PersistentDataType<P, C> type, C defaultValue) {
        Preconditions.checkArgument(key != null, "key cannot be null");
        Preconditions.checkArgument(type != null, "type cannot be null");
        Preconditions.checkArgument(defaultValue != null, "defaultValue cannot be null");

        C value = get(key, type);
        return value != null ? value : defaultValue;
    }

    @Override
    public Set<String> getKeys() {
        return Set.copyOf(getCompound().a.keySet());
    }

    @Override
    public boolean isEmpty() {
        return getCompound().a.isEmpty();
    }

    @Override
    public void copyTo(PersistentDataContainer other, boolean replace) {
        Preconditions.checkArgument(other != null, "other cannot be null");

        PersistentDataContainerImpl target = (PersistentDataContainerImpl) other;
        if (replace) {
            target.getCompound().a.putAll(getCompound().a);
        } else {
            getCompound().a.forEach(target.getCompound().a::putIfAbsent);
        }
    }

    public NBTTagCompound getCompound() {
        return this.compound;
    }
}
