package org.bukkit.plugin.java;

import org.bukkit.plugin.PluginDescriptionFile;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A ClassLoader for plugins, to allow shared classes across multiple plugins
 */
public class PluginClassLoader extends URLClassLoader {
    private final JavaPluginLoader loader;
    private final Map<String, Class<?>> classes = new HashMap<>();
    public final JavaPlugin plugin; // Poseidon

    // Poseidon - change signature
    public PluginClassLoader(final JavaPluginLoader loader, final URL[] urls, final ClassLoader parent, final PluginDescriptionFile description) throws ReflectiveOperationException {
        super(urls, parent);

        this.loader = loader;

        // Poseidon start
        Class<?> jarClass = Class.forName(description.getMain(), true, this);
        Class<? extends JavaPlugin> plugin = jarClass.asSubclass(JavaPlugin.class);
        Constructor<? extends JavaPlugin> constructor = plugin.getConstructor();
        this.plugin = constructor.newInstance();
        // Poseidon end
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return findClass(name, true);
    }

    protected Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException {
        Class<?> result = classes.get(name);

        if (result == null) {
            if (checkGlobal) {
                result = loader.getClassByName(name);
            }

            if (result == null) {
                result = super.findClass(name);

                if (result != null) {
                    loader.setClass(name, result);
                }
            }

            classes.put(name, result);
        }

        return result;
    }

    public Set<String> getClasses() {
        return classes.keySet();
    }
}
