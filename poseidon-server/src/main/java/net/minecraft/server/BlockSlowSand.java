package net.minecraft.server;

public class BlockSlowSand extends Block {

    public BlockSlowSand(int i, int j) {
        super(i, j, Material.SAND);
    }

    public AxisAlignedBB e(World world, int i, int j, int k) {
        float f = 0.125F;

        return AxisAlignedBB.b(i, j, k, i + 1, (float) (j + 1) - f, k + 1);
    }

    public void a(World world, int i, int j, int k, Entity entity) {
        entity.motX *= 0.4D;
        entity.motZ *= 0.4D;
    }
}
