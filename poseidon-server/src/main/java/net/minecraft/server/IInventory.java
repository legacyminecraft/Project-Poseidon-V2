package net.minecraft.server;

import org.jspecify.annotations.Nullable;

public interface IInventory {

    int getSize();

    @Nullable ItemStack getItem(int i);

    @Nullable ItemStack splitStack(int i, int j);

    void setItem(int i, @Nullable ItemStack itemstack);

    String getName();

    int getMaxStackSize();

    void update();

    boolean a_(EntityHuman entityhuman);

    @Nullable ItemStack[] getContents(); // CraftBukkit
}
