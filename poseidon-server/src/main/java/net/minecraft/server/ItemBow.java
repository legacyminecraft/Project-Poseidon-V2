package net.minecraft.server;

public class ItemBow extends Item {

    public ItemBow(int i) {
        super(i);
        this.maxStackSize = 1;
    }

    public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
        int arrowSlot = entityhuman.inventory.b(Item.ARROW.id); // Poseidon
        if (arrowSlot >= 0) {
            // Poseidon - fix player bow sounds
            world.a(entityhuman, 1002, MathHelper.floor(entityhuman.locX), MathHelper.floor(entityhuman.locY - (double) entityhuman.height), MathHelper.floor(entityhuman.locZ), 0);
            if (!world.isStatic) {
                world.addEntity(new EntityArrow(world, entityhuman));
            }

            // Poseidon start - send slot update
            if (entityhuman instanceof EntityPlayer player) {
                Slot slot = player.activeContainer.a(player.inventory, arrowSlot);
                if (slot != null) {
                    player.netServerHandler.sendPacket(new Packet103SetSlot(player.activeContainer.windowId, slot.a, player.inventory.items[arrowSlot]));
                }
            }
            // Poseidon end
        }

        return itemstack;
    }
}
