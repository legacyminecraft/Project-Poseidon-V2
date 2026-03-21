package net.minecraft.server;

import org.jspecify.annotations.Nullable;

public interface IBlockAccess {

    int getTypeId(int i, int j, int k);

    @Nullable TileEntity getTileEntity(int i, int j, int k);

    int getData(int i, int j, int k);

    Material getMaterial(int i, int j, int k);

    boolean e(int i, int j, int k);
}
