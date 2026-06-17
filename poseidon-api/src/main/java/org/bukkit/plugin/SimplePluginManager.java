package org.bukkit.plugin;

import com.google.common.base.Preconditions;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.FileUtil;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles all plugin management from the Server
 */
public final class SimplePluginManager implements PluginManager {
    private final Server server;
    private final Map<Pattern, PluginLoader> fileAssociations = new HashMap<>();
    private final List<Plugin> plugins = new ArrayList<>();
    private final Map<String, Plugin> lookupNames = new HashMap<>();
    //private final Map<Event.Type, SortedSet<RegisteredListener>> listeners = new EnumMap<>(Event.Type.class); // Poseidon - remove
    private static @Nullable File updateDirectory = null;
    private final SimpleCommandMap commandMap;
    private final Map<String, Permission> permissions = new HashMap<>();
    private final Map<Boolean, Set<Permission>> defaultPerms = new LinkedHashMap<>();
    private final Map<String, Map<Permissible, Boolean>> permSubs = new HashMap<>();
    private final Map<Boolean, Map<Permissible, Boolean>> defSubs = new HashMap<>();
    // Poseidon start - remove
    /*private final Comparator<RegisteredListener> comparer = new Comparator<>() {
        public int compare(RegisteredListener i, RegisteredListener j) {
            int result = i.getPriority().compareTo(j.getPriority());

            if ((result == 0) && (i != j)) {
                result = 1;
            }

            return result;
        }
    };*/
    // Poseidon end

    public SimplePluginManager(Server instance, SimpleCommandMap commandMap) {
        server = instance;
        this.commandMap = commandMap;

        defaultPerms.put(true, new HashSet<>());
        defaultPerms.put(false, new HashSet<>());
    }

    /**
     * Registers the specified plugin loader
     *
     * @param loader Class name of the PluginLoader to register
     * @throws IllegalArgumentException Thrown when the given Class is not a valid PluginLoader
     */
    public void registerInterface(Class<? extends PluginLoader> loader) throws IllegalArgumentException {
        PluginLoader instance;

        if (PluginLoader.class.isAssignableFrom(loader)) {
            Constructor<? extends PluginLoader> constructor;

            try {
                constructor = loader.getConstructor(Server.class);
                instance = constructor.newInstance(server);
            } catch (NoSuchMethodException ex) {
                String className = loader.getName();

                throw new IllegalArgumentException(String.format("Class %s does not have a public %s(Server) constructor", className, className), ex);
            } catch (Exception ex) {
                throw new IllegalArgumentException(String.format("Unexpected exception %s while attempting to construct a new instance of %s", ex.getClass().getName(), loader.getName()), ex);
            }
        } else {
            throw new IllegalArgumentException(String.format("Class %s does not implement interface PluginLoader", loader.getName()));
        }

        Pattern[] patterns = instance.getPluginFileFilters();

        synchronized (this) {
            for (Pattern pattern : patterns) {
                fileAssociations.put(pattern, instance);
            }
        }
    }

    /**
     * Loads the plugins contained within the specified directory
     *
     * @param directory Directory to check for plugins
     * @return A list of all plugins loaded
     */
    public Plugin[] loadPlugins(File directory) {
        List<Plugin> result = new ArrayList<>();
        Set<Pattern> filters = fileAssociations.keySet();

        if (!(server.getUpdateFolder().equals(""))) {
            updateDirectory = new File(directory, server.getUpdateFolder());
        }

        // Poseidon start - backport plugin load ordering from newer Bukkit
        Set<String> loadedPlugins = new HashSet<>();

        // This is where it figures out all possible plugins
        PluginFiles files = listPlugins(directory, filters);

        while (!files.plugins.isEmpty()) {
            boolean missingDependency = true;
            Iterator<String> pluginIterator = files.plugins.keySet().iterator();

            while (pluginIterator.hasNext()) {
                String plugin = pluginIterator.next();

                if (files.dependencies.containsKey(plugin)) {
                    Iterator<String> dependencyIterator = files.dependencies.get(plugin).iterator();

                    while (dependencyIterator.hasNext()) {
                        String dependency = dependencyIterator.next();

                        // Dependency loaded
                        if (loadedPlugins.contains(dependency)) {
                            dependencyIterator.remove();

                            // We have a dependency not found
                        } else if (!files.plugins.containsKey(dependency)) {
                            missingDependency = false;
                            Path path = files.plugins.get(plugin);
                            pluginIterator.remove();
                            files.softDependencies.remove(plugin);
                            files.dependencies.remove(plugin);

                            server.getLogger().log(
                                    Level.SEVERE,
                                    "Could not load '" + path + "' in folder '" + directory.getPath() + "'",
                                    new UnknownDependencyException(dependency));
                            break;
                        }
                    }

                    if (files.dependencies.containsKey(plugin) && files.dependencies.get(plugin).isEmpty()) {
                        files.dependencies.remove(plugin);
                    }
                }
                if (files.softDependencies.containsKey(plugin)) {
                    Iterator<String> softDependencyIterator = files.softDependencies.get(plugin).iterator();

                    while (softDependencyIterator.hasNext()) {
                        String softDependency = softDependencyIterator.next();

                        // Soft depend is no longer around
                        if (!files.plugins.containsKey(softDependency)) {
                            softDependencyIterator.remove();
                        }
                    }

                    if (files.softDependencies.get(plugin).isEmpty()) {
                        files.softDependencies.remove(plugin);
                    }
                }
                if (!(files.dependencies.containsKey(plugin) || files.softDependencies.containsKey(plugin)) && files.plugins.containsKey(plugin)) {
                    // We're clear to load, no more soft or hard dependencies left
                    Path path = files.plugins.get(plugin);
                    pluginIterator.remove();
                    missingDependency = false;

                    try {
                        result.add(loadPlugin(path.toFile()));
                        loadedPlugins.add(plugin);
                    } catch (InvalidPluginException | UnknownDependencyException ex) {
                        server.getLogger().log(Level.SEVERE, "Could not load '" + path + "' in folder '" + directory.getPath() + "'", ex);
                    }
                }
            }

            if (missingDependency) {
                // We now iterate over plugins until something loads
                // This loop will ignore soft dependencies
                pluginIterator = files.plugins.keySet().iterator();

                while (pluginIterator.hasNext()) {
                    String plugin = pluginIterator.next();

                    if (!files.dependencies.containsKey(plugin)) {
                        files.softDependencies.remove(plugin);
                        missingDependency = false;
                        Path path = files.plugins.get(plugin);
                        pluginIterator.remove();

                        try {
                            result.add(loadPlugin(path.toFile()));
                            loadedPlugins.add(plugin);
                            break;
                        } catch (InvalidPluginException | UnknownDependencyException ex) {
                            server.getLogger().log(Level.SEVERE, "Could not load '" + path + "' in folder '" + directory.getPath() + "'", ex);
                        }
                    }
                }
                // We have no plugins left without a depend
                if (missingDependency) {
                    files.softDependencies.clear();
                    files.dependencies.clear();
                    Iterator<Path> failedPluginIterator = files.plugins.values().iterator();

                    while (failedPluginIterator.hasNext()) {
                        Path path = failedPluginIterator.next();
                        failedPluginIterator.remove();
                        server.getLogger().log(Level.SEVERE, "Could not load '" + path + "' in folder '" + directory.getPath() + "': circular dependency detected");
                    }
                }
            }
        }

        return result.toArray(new Plugin[result.size()]);
    }

    PluginFiles listPlugins(File directory, Set<Pattern> filters) {
        Map<String, Path> plugins = new HashMap<>();
        Map<String, Collection<String>> dependencies = new HashMap<>();
        Map<String, Collection<String>> softDependencies = new HashMap<>();

        for (File file : directory.listFiles()) {
            PluginLoader loader = null;
            for (Pattern filter : filters) {
                if (filter.matcher(file.getName()).find()) {
                    loader = fileAssociations.get(filter);
                }
            }

            if (loader == null) {
                continue;
            }

            PluginDescriptionFile description;
            try {
                description = loader.getPluginDescription(file);
            } catch (InvalidDescriptionException ex) {
                server.getLogger().log(Level.SEVERE, "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "'", ex);
                continue;
            }

            Path replacedFile = plugins.put(description.getName(), file.toPath());
            if (replacedFile != null) {
                server.getLogger().severe(String.format(
                    "Ambiguous plugin name `%s' for files `%s' and `%s' in `%s'",
                    description.getName(),
                    file.getPath(),
                    replacedFile,
                    directory.getPath()
                ));
            }

            Collection<String> softDependencySet = description.getSoftDepend();
            if (softDependencySet != null && !softDependencySet.isEmpty()) {
                if (softDependencies.containsKey(description.getName())) {
                    // Duplicates do not matter, they will be removed together if applicable
                    softDependencies.get(description.getName()).addAll(softDependencySet);
                } else {
                    softDependencies.put(description.getName(), new LinkedList<>(softDependencySet));
                }
            }

            Collection<String> dependencySet = description.getDepend();
            if (dependencySet != null && !dependencySet.isEmpty()) {
                dependencies.put(description.getName(), new LinkedList<>(dependencySet));
            }

            Collection<String> loadBeforeSet = description.getLoadBefore();
            if (loadBeforeSet != null && !loadBeforeSet.isEmpty()) {
                for (String loadBeforeTarget : loadBeforeSet) {
                    if (softDependencies.containsKey(loadBeforeTarget)) {
                        softDependencies.get(loadBeforeTarget).add(description.getName());
                    } else {
                        // softDependencies is never iterated, so 'ghost' plugins aren't an issue
                        Collection<String> shortSoftDependency = new LinkedList<>();
                        shortSoftDependency.add(description.getName());
                        softDependencies.put(loadBeforeTarget, shortSoftDependency);
                    }
                }
            }
        }

        return new PluginFiles(plugins, dependencies, softDependencies);
    }

    record PluginFiles(
            Map<String, Path> plugins,
            Map<String, Collection<String>> dependencies,
            Map<String, Collection<String>> softDependencies
    ) {
    }
    // Poseidon end

    /**
     * Loads the plugin in the specified file
     *
     * File must be valid according to the current enabled Plugin interfaces
     *
     * @param file File containing the plugin to load
     * @return The Plugin loaded, or null if it was invalid
     * @throws InvalidPluginException Thrown when the specified file is not a valid plugin
     */
    public synchronized @Nullable Plugin loadPlugin(File file) throws InvalidPluginException, UnknownDependencyException { // Poseidon - remove InvalidDescriptionException
        return loadPlugin(file, true);
    }

    /**
     * Loads the plugin in the specified file
     *
     * File must be valid according to the current enabled Plugin interfaces
     *
     * @param file File containing the plugin to load
     * @param ignoreSoftDependencies Loader will ignore soft dependencies if this flag is set to true
     * @return The Plugin loaded, or null if it was invalid
     * @throws InvalidPluginException Thrown when the specified file is not a valid plugin
     */
    public synchronized @Nullable Plugin loadPlugin(File file, boolean ignoreSoftDependencies) throws InvalidPluginException, UnknownDependencyException { // Poseidon - remove InvalidDescriptionException
        File updateFile = null;

        if (updateDirectory != null && updateDirectory.isDirectory() && (updateFile = new File(updateDirectory, file.getName())).isFile()) {
            if (FileUtil.copy(updateFile, file)) {
                // Poseidon - add log for replacing plugin file from update directory
                server.getLogger().info("An updated file for \"" + file.getName() + "\" has been found in the update folder. Replaced the original file.");
                updateFile.delete();
            }
        }

        Set<Pattern> filters = fileAssociations.keySet();
        Plugin result = null;

        for (Pattern filter : filters) {
            String name = file.getName();
            Matcher match = filter.matcher(name);

            if (match.find()) {
                PluginLoader loader = fileAssociations.get(filter);

                result = loader.loadPlugin(file, ignoreSoftDependencies);
            }
        }

        if (result != null) {
            plugins.add(result);
            lookupNames.put(result.getDescription().getName(), result);
        }

        return result;
    }

    /**
     * Checks if the given plugin is loaded and returns it when applicable
     *
     * Please note that the name of the plugin is case-sensitive
     *
     * @param name Name of the plugin to check
     * @return Plugin if it exists, otherwise null
     */
    public synchronized @Nullable Plugin getPlugin(String name) {
        return lookupNames.get(name);
    }

    public synchronized Plugin[] getPlugins() {
        return plugins.toArray(new Plugin[0]);
    }

    /**
     * Checks if the given plugin is enabled or not
     *
     * Please note that the name of the plugin is case-sensitive.
     *
     * @param name Name of the plugin to check
     * @return true if the plugin is enabled, otherwise false
     */
    public boolean isPluginEnabled(String name) {
        Plugin plugin = getPlugin(name);

        return isPluginEnabled(plugin);
    }

    /**
     * Checks if the given plugin is enabled or not
     *
     * @param plugin Plugin to check
     * @return true if the plugin is enabled, otherwise false
     */
    public boolean isPluginEnabled(@Nullable Plugin plugin) {
        if ((plugin != null) && (plugins.contains(plugin))) {
            return plugin.isEnabled();
        } else {
            return false;
        }
    }

    public void enablePlugin(final Plugin plugin) {
        if (!plugin.isEnabled()) {
            List<Command> pluginCommands = PluginCommandYamlParser.parse(plugin);

            if (!pluginCommands.isEmpty()) {
                commandMap.registerAll(plugin.getDescription().getName(), pluginCommands);
            }
            
            try {
                plugin.getPluginLoader().enablePlugin(plugin);
            } catch (Throwable ex) {
                server.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while enabling " + plugin.getDescription().getFullName() + " (Is it up to date?): " + ex.getMessage(), ex);
            }

            HandlerList.bakeAll(); // Poseidon
        }
    }

    public void disablePlugins() {
        // Poseidon start - reverse disabling order
        Plugin[] plugins = getPlugins();
        for (int i = plugins.length - 1; i >= 0; i--) {
            disablePlugin(plugins[i]);
        }
        // Poseidon end
    }

    public void disablePlugin(final Plugin plugin) {
        if (plugin.isEnabled()) {
            try {
                plugin.getPluginLoader().disablePlugin(plugin);
            } catch (Throwable ex) {
                server.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while disabling " + plugin.getDescription().getFullName() + " (Is it up to date?): " + ex.getMessage(), ex);
            }

            try {
                server.getScheduler().cancelTasks(plugin);
            } catch (Throwable ex) {
                server.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while cancelling tasks for " + plugin.getDescription().getFullName() + " (Is it up to date?): " + ex.getMessage(), ex);
            }

            try {
                server.getServicesManager().unregisterAll(plugin);
            } catch (Throwable ex) {
                server.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while unregistering services for " + plugin.getDescription().getFullName() + " (Is it up to date?): " + ex.getMessage(), ex);
            }

            // Poseidon start
            try {
                HandlerList.unregisterAll(plugin);
            } catch (Throwable ex) {
                server.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while unregistering events for " + plugin.getDescription().getFullName() + " (Is it up to date?): " + ex.getMessage(), ex);
            }

            try {
                server.getMessenger().unregisterInboundChannels(plugin);
                server.getMessenger().unregisterOutboundChannels(plugin);
            } catch (Throwable ex) {
                server.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while unregistering plugin channels for " + plugin.getDescription().getFullName() + " (Is it up to date?): " + ex.getMessage(), ex);
            }
            // Poseidon end
        }
    }

    public void clearPlugins() {
        synchronized (this) {
            disablePlugins();
            plugins.clear();
            lookupNames.clear();
            HandlerList.unregisterAll(); // Poseidon
            fileAssociations.clear();
            permissions.clear();
            defaultPerms.get(true).clear();
            defaultPerms.get(false).clear();
        }
    }

    /**
     * Calls an event with the given details
     *
     * @param event Event details
     */
    public void callEvent(Event event) { // Poseidon - not synchronized
        // Poseidon start
        if (event.isAsynchronous()) {
            if (Thread.holdsLock(this)) {
                throw new IllegalStateException(event.getEventName() + " cannot be triggered asynchronously from inside synchronized code.");
            }
            if (server.isPrimaryThread()) {
                throw new IllegalStateException(event.getEventName() + " cannot be triggered asynchronously from primary server thread.");
            }
            fireEvent(event);
        } else {
            synchronized (this) {
                fireEvent(event);
            }
        }
    }

    private void fireEvent(Event event) {
        HandlerList handlers = event.getHandlers();
        RegisteredListener[] listeners = handlers.getRegisteredListeners();

        for (int i = 0; i < listeners.length; i++) {
            RegisteredListener registration = listeners[i];
            if (!registration.getPlugin().isEnabled()) {
                continue;
            }

            try {
                registration.callEvent(event);
            } catch (Throwable ex) {
                server.getLogger().log(Level.SEVERE, "Could not pass event " + event.getEventName() + " to " + registration.getPlugin().getDescription().getName(), ex);
            }
        }
    }
    // Poseidon end

    /**
     * Registers the given event to the specified listener
     *
     * @param type EventType to register
     * @param listener PlayerListener to register
     * @param priority Priority of this event
     * @param plugin Plugin to register
     */
    @Deprecated // Poseidon - deprecate
    public void registerEvent(Event.Type type, Listener listener, Priority priority, Plugin plugin) {
        registerEvent(type.getEventClass(), listener, priority.getNewPriority(), plugin.getPluginLoader().createExecutor(type, listener), plugin); // Poseidon
    }

    /**
     * Registers the given event to the specified listener using a directly passed EventExecutor
     *
     * @param type EventType to register
     * @param listener PlayerListener to register
     * @param executor EventExecutor to register
     * @param priority Priority of this event
     * @param plugin Plugin to register
     */
    @Deprecated // Poseidon - deprecate
    public void registerEvent(Event.Type type, Listener listener, EventExecutor executor, Priority priority, Plugin plugin) {
        registerEvent(type.getEventClass(), listener, priority.getNewPriority(), executor, plugin); // Poseidon
    }

    // Poseidon start
    @Override
    public void registerEvents(Listener listener, Plugin plugin) {
        if (!plugin.isEnabled()) {
            throw new IllegalPluginAccessException("Plugin attempted to register " + listener + " while not enabled");
        }

        for (Map.Entry<Class<? extends Event>, Set<RegisteredListener>> entry : plugin.getPluginLoader().createRegisteredListeners(listener, plugin).entrySet()) {
            getEventListeners(getRegistrationClass(entry.getKey())).registerAll(entry.getValue());
        }
    }

    @Override
    public void registerEvent(Class<? extends Event> event, Listener listener, EventPriority priority, EventExecutor executor, Plugin plugin) {
        registerEvent(event, listener, priority, executor, plugin, false);
    }

    public void registerEvent(Class<? extends Event> event, Listener listener, EventPriority priority, EventExecutor executor, Plugin plugin, boolean ignoreCancelled) {
        Preconditions.checkArgument(listener != null, "listener cannot be null");
        Preconditions.checkArgument(priority != null, "priority cannot be null");
        Preconditions.checkArgument(executor != null, "executor cannot be null");
        Preconditions.checkArgument(plugin != null, "plugin cannot be null");

        if (!plugin.isEnabled()) {
            throw new IllegalPluginAccessException("Plugin attempted to register " + event + " while not enabled");
        }

        getEventListeners(event).register(new RegisteredListener(listener, executor, priority, plugin, ignoreCancelled));
    }

    private HandlerList getEventListeners(Class<? extends Event> type) {
        try {
            Method method = getRegistrationClass(type).getDeclaredMethod("getHandlerList");
            method.setAccessible(true);
            return (HandlerList) method.invoke(null);
        } catch (Exception e) {
            throw new IllegalPluginAccessException(e.toString());
        }
    }

    private Class<? extends Event> getRegistrationClass(Class<? extends Event> clazz) {
        try {
            clazz.getDeclaredMethod("getHandlerList");
            return clazz;
        } catch (NoSuchMethodException e) {
            if (clazz.getSuperclass() != null
                    && !clazz.getSuperclass().equals(Event.class)
                    && Event.class.isAssignableFrom(clazz.getSuperclass())) {
                return getRegistrationClass(clazz.getSuperclass().asSubclass(Event.class));
            } else {
                throw new IllegalPluginAccessException("Unable to find handler list for event " + clazz.getName() + ". Static getHandlerList method required!");
            }
        }
    }
    // Poseidon end

    public @Nullable Permission getPermission(String name) {
        return permissions.get(name.toLowerCase());
    }

    public void addPermission(Permission perm) {
        String name = perm.getName().toLowerCase();

        if (permissions.containsKey(name)) {
            throw new IllegalArgumentException("The permission " + name + " is already defined!");
        }

        permissions.put(name, perm);
        calculatePermissionDefault(perm);
    }

    public Set<Permission> getDefaultPermissions(boolean op) {
        return Set.copyOf(defaultPerms.get(op)); // Poseidon - use JDK set
    }

    public void removePermission(Permission perm) {
        removePermission(perm.getName().toLowerCase());
    }

    public void removePermission(String name) {
        permissions.remove(name);
    }

    public void recalculatePermissionDefaults(Permission perm) {
        if (permissions.containsValue(perm)) {
            defaultPerms.get(true).remove(perm);
            defaultPerms.get(false).remove(perm);

            calculatePermissionDefault(perm);
        }
    }

    private void calculatePermissionDefault(Permission perm) {
        if ((perm.getDefault() == PermissionDefault.OP) || (perm.getDefault() == PermissionDefault.TRUE)) {
            defaultPerms.get(true).add(perm);
            dirtyPermissibles(true);
        }
        if ((perm.getDefault() == PermissionDefault.NOT_OP) || (perm.getDefault() == PermissionDefault.TRUE)) {
            defaultPerms.get(false).add(perm);
            dirtyPermissibles(false);
        }
    }

    private void dirtyPermissibles(boolean op) {
        Set<Permissible> permissibles = getDefaultPermSubscriptions(op);

        for (Permissible p : permissibles) {
            p.recalculatePermissions();
        }
    }

    public void subscribeToPermission(String permission, Permissible permissible) {
        String name = permission.toLowerCase();
        Map<Permissible, Boolean> map = permSubs.get(name);

        if (map == null) {
            map = new WeakHashMap<>(); // Poseidon - use WeakHashMap
            permSubs.put(name, map);
        }

        map.put(permissible, true);
    }

    public void unsubscribeFromPermission(String permission, Permissible permissible) {
        String name = permission.toLowerCase();
        Map<Permissible, Boolean> map = permSubs.get(name);

        if (map != null) {
            map.remove(permissible);

            if (map.isEmpty()) {
                permSubs.remove(name);
            }
        }
    }

    public Set<Permissible> getPermissionSubscriptions(String permission) {
        String name = permission.toLowerCase();
        Map<Permissible, Boolean> map = permSubs.get(name);

        if (map == null) {
            // Poseidon start - use JDK set
            return Set.of();
        } else {
            return Set.copyOf(map.keySet());
            // Poseidon end
        }
    }

    public void subscribeToDefaultPerms(boolean op, Permissible permissible) {
        Map<Permissible, Boolean> map = defSubs.get(op);

        if (map == null) {
            map = new WeakHashMap<>(); // Poseidon - use WeakHashMap
            defSubs.put(op, map);
        }

        map.put(permissible, true);
    }

    public void unsubscribeFromDefaultPerms(boolean op, Permissible permissible) {
        Map<Permissible, Boolean> map = defSubs.get(op);

        if (map != null) {
            map.remove(permissible);

            if (map.isEmpty()) {
                defSubs.remove(op);
            }
        }
    }

    public Set<Permissible> getDefaultPermSubscriptions(boolean op) {
        Map<Permissible, Boolean> map = defSubs.get(op);

        if (map == null) {
            // Poseidon start - use JDK set
            return Set.of();
        } else {
            return Set.copyOf(map.keySet());
            // Poseidon end
        }
    }

    public Set<Permission> getPermissions() {
        return new HashSet<>(permissions.values());
    }
}
