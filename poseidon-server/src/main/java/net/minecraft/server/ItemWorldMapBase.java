package net.minecraft.server;

import org.jspecify.annotations.Nullable;

public class ItemWorldMapBase extends Item {

    protected ItemWorldMapBase(int i) {
        super(i);
    }

    public boolean b() {
        return true;
    }

    public @Nullable Packet b(ItemStack itemstack, World world, EntityHuman entityhuman) {
        return null;
    }
}
