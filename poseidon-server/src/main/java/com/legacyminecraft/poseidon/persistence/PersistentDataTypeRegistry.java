package com.legacyminecraft.poseidon.persistence;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import net.minecraft.server.NBTBase;
import net.minecraft.server.NBTTagByte;
import net.minecraft.server.NBTTagByteArray;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagDouble;
import net.minecraft.server.NBTTagFloat;
import net.minecraft.server.NBTTagInt;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagLong;
import net.minecraft.server.NBTTagShort;
import net.minecraft.server.NBTTagString;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class PersistentDataTypeRegistry {

    private final Map<PersistentDataType<?, ?>, TagAdapter<?, ?>> adapters = new ConcurrentHashMap<>();

    public <P, T extends NBTBase> TagAdapter<P, T> getOrCreateAdapter(PersistentDataType<P, ?> type) {
        return (TagAdapter<P, T>) this.adapters.computeIfAbsent(type, _ -> createAdapter(type));
    }

    private <P> TagAdapter createAdapter(PersistentDataType<P, ?> type) {
        PrimitiveType<P> primitiveType = type.getPrimitiveType();

        if (Objects.equals(PrimitiveType.BYTE, primitiveType)) {
            return new TagAdapter<Byte, NBTTagByte>(
                    NBTTagByte::new, tag -> tag.a, tag -> tag instanceof NBTTagByte
            );
        } else if (Objects.equals(PrimitiveType.SHORT, primitiveType)) {
            return new TagAdapter<Short, NBTTagShort>(
                    NBTTagShort::new, tag -> tag.a, tag -> tag instanceof NBTTagShort
            );
        } else if (Objects.equals(PrimitiveType.INTEGER, primitiveType)) {
            return new TagAdapter<Integer, NBTTagInt>(
                    NBTTagInt::new, tag -> tag.a, tag -> tag instanceof NBTTagInt
            );
        } else if (Objects.equals(PrimitiveType.LONG, primitiveType)) {
            return new TagAdapter<Long, NBTTagLong>(
                    NBTTagLong::new, tag -> tag.a, tag -> tag instanceof NBTTagLong
            );
        } else if (Objects.equals(PrimitiveType.FLOAT, primitiveType)) {
            return new TagAdapter<Float, NBTTagFloat>(
                    NBTTagFloat::new, tag -> tag.a, tag -> tag instanceof NBTTagFloat
            );
        } else if (Objects.equals(PrimitiveType.DOUBLE, primitiveType)) {
            return new TagAdapter<Double, NBTTagDouble>(
                    NBTTagDouble::new, tag -> tag.a, tag -> tag instanceof NBTTagDouble
            );
        } else if (Objects.equals(PrimitiveType.STRING, primitiveType)) {
            return new TagAdapter<String, NBTTagString>(
                    NBTTagString::new, tag -> tag.a, tag -> tag instanceof NBTTagString
            );
        } else if (Objects.equals(PrimitiveType.BYTE_ARRAY, primitiveType)) {
            return new TagAdapter<byte[], NBTTagByteArray>(
                    NBTTagByteArray::new, tag -> tag.a, tag -> tag instanceof NBTTagByteArray
            );
        } else if (Objects.equals(PrimitiveType.CONTAINER, primitiveType)) {
            return new TagAdapter<>(
                    PersistentDataContainerImpl::getCompound, PersistentDataContainerImpl::new, tag -> tag instanceof NBTTagCompound
            );
        } else if (Objects.equals(PrimitiveType.LIST, primitiveType)) {
            Preconditions.checkArgument(type instanceof ListPersistentDataType, "type must be a ListPersistentDataType");
            ListPersistentDataType<?, ?> listType = (ListPersistentDataType<?, ?>) type;
            TagAdapter elementAdapter = getOrCreateAdapter(listType.getElementType());

            return new TagAdapter<List, NBTTagList>(
                    list -> {
                        NBTTagList tag = new NBTTagList();
                        list.forEach(p -> tag.a(elementAdapter.serialize(p)));
                        return tag;
                    },
                    tag -> Lists.transform(tag.a, e -> {
                        Preconditions.checkState(elementAdapter.matches(e));
                        return elementAdapter.deserialize(e);
                    }),
                    tag -> tag instanceof NBTTagList
            );
        } else {
            throw new IllegalArgumentException("illegal primitive type: " + primitiveType.typeClass().getName());
        }
    }
}
