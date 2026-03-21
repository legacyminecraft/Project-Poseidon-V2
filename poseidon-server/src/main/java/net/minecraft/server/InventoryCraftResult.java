package net.minecraft.server;

import org.jspecify.annotations.Nullable;

public class InventoryCraftResult implements IInventory {

    private @Nullable ItemStack[] items = new ItemStack[1];

    // CraftBukkit start
    public @Nullable ItemStack[] getContents() {
        return this.items;
    }
    // CraftBukkit end

    public InventoryCraftResult() {}

    public int getSize() {
        return 1;
    }

    public @Nullable ItemStack getItem(int i) {
        return this.items[i];
    }

    public String getName() {
        return "Result";
    }

    public @Nullable ItemStack splitStack(int i, int j) {
        if (this.items[i] != null) {
            ItemStack itemstack = this.items[i];

            this.items[i] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    public void setItem(int i, @Nullable ItemStack itemstack) {
        this.items[i] = itemstack;
    }

    public int getMaxStackSize() {
        return 64;
    }

    public void update() {}

    public boolean a_(EntityHuman entityhuman) {
        return true;
    }
}
