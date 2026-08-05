package com.legacyminecraft.poseidon.performance.spark;

import me.lucko.spark.common.sampler.source.ClassSourceLookup;
import org.bukkit.plugin.java.PluginClassLoader;
import org.jspecify.annotations.Nullable;

public final class PoseidonClassSourceLookup extends ClassSourceLookup.ByClassLoader {

    @Override
    public @Nullable String identify(ClassLoader loader) {
        if (loader instanceof PluginClassLoader pluginClassLoader) {
            return pluginClassLoader.plugin.getDescription().getName();
        }
        return null;
    }
}
