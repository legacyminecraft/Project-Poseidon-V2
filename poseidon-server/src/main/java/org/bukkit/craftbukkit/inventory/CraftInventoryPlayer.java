package org.bukkit.craftbukkit.inventory;

import net.minecraft.server.InventoryPlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jspecify.annotations.Nullable;

public class CraftInventoryPlayer extends CraftInventory implements PlayerInventory {
    public CraftInventoryPlayer(net.minecraft.server.InventoryPlayer inventory) {
        super(inventory);
    }

    public InventoryPlayer getInventory() {
        return (InventoryPlayer) inventory;
    }

    public int getSize() {
        return super.getSize() - 4;
    }

    public ItemStack getItemInHand() {
        return new CraftItemStack(getInventory().getItemInHand());
    }

    public void setItemInHand(@Nullable ItemStack stack) {
        setItem(getHeldItemSlot(), stack);
    }

    public int getHeldItemSlot() {
        return getInventory().itemInHandIndex;
    }

    public ItemStack getHelmet() {
        return getItem(getSize() + 3);
    }

    public ItemStack getChestplate() {
        return getItem(getSize() + 2);
    }

    public ItemStack getLeggings() {
        return getItem(getSize() + 1);
    }

    public ItemStack getBoots() {
        return getItem(getSize() + 0);
    }

    public void setHelmet(@Nullable ItemStack helmet) {
        setItem(getSize() + 3, helmet);
    }

    public void setChestplate(@Nullable ItemStack chestplate) {
        setItem(getSize() + 2, chestplate);
    }

    public void setLeggings(@Nullable ItemStack leggings) {
        setItem(getSize() + 1, leggings);
    }

    public void setBoots(@Nullable ItemStack boots) {
        setItem(getSize() + 0, boots);
    }

    public @Nullable CraftItemStack[] getArmorContents() {
        net.minecraft.server.@Nullable ItemStack[] mcItems = getInventory().getArmorContents();
        @Nullable CraftItemStack[] ret = new CraftItemStack[mcItems.length];

        for (int i = 0; i < mcItems.length; i++) {
            ret[i] = new CraftItemStack(mcItems[i]);
        }
        return ret;
    }

    public void setArmorContents(@Nullable ItemStack[] items) {
        int cnt = getSize();

        if (items == null) {
            items = new ItemStack[4];
        }
        for (ItemStack item : items) {
            if (item == null || item.getTypeId() == 0) {
                clear(cnt++);
            } else {
                setItem(cnt++, item);
            }
        }
    }
}
