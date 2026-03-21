package net.minecraft.server;

import org.jspecify.annotations.Nullable;

import java.util.List;

public interface ICrafting {

    void a(Container container, List<@Nullable ItemStack> list);

    void a(Container container, int i, @Nullable ItemStack itemstack);

    void a(Container container, int i, int j);
}
