package net.minecraft.server;

public class ItemBow extends Item {

    public ItemBow(int i) {
        super(i);
        this.maxStackSize = 1;
    }

    public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
        if (entityhuman.inventory.b(Item.ARROW.id)) {
            // Poseidon - fix player bow sounds
            world.a(entityhuman, 1002, MathHelper.floor(entityhuman.locX), MathHelper.floor(entityhuman.locY - (double) entityhuman.height), MathHelper.floor(entityhuman.locZ), 0);
            if (!world.isStatic) {
                world.addEntity(new EntityArrow(world, entityhuman));
            }
        }

        return itemstack;
    }
}
