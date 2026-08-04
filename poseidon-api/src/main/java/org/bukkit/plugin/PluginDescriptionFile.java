package org.bukkit.plugin;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.jspecify.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * Provides access to a Plugins description file, plugin.yaml
 */
public final class PluginDescriptionFile {
    private static final Yaml yaml = new Yaml(new SafeConstructor());
    private String name = null;
    private String main = null;
    private List<String> depend = List.of(); // Poseidon - ArrayList -> List
    private List<String> softDepend = List.of(); // Poseidon - ArrayList -> List
    private List<String> loadBefore = List.of(); // Poseidon
    private String version = null;
    private @Nullable Object commands = null;
    private @Nullable String description = null;
    private @Nullable String prefix = null; // Poseidon
    private ArrayList<String> authors = new ArrayList<>();
    private @Nullable String website = null;
    //private boolean database = false; // Poseidon
    private PluginLoadOrder order = PluginLoadOrder.POSTWORLD;
    private ArrayList<Permission> permissions = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public PluginDescriptionFile(final InputStream stream) throws InvalidDescriptionException {
        loadMap((Map<String, Object>) yaml.load(stream));
    }

    /**
     * Loads a PluginDescriptionFile from the specified reader
     * @param reader
     */
    @SuppressWarnings("unchecked")
    public PluginDescriptionFile(final Reader reader) throws InvalidDescriptionException {
        loadMap((Map<String, Object>) yaml.load(reader));
    }

    /**
     * Creates a new PluginDescriptionFile with the given detailed
     *
     * @param pluginName Name of this plugin
     * @param mainClass Full location of the main class of this plugin
     */
    public PluginDescriptionFile(final String pluginName, final String pluginVersion, final String mainClass) {
        name = pluginName;
        version = pluginVersion;
        main = mainClass;
    }

    /**
     * Saves this PluginDescriptionFile to the given writer
     *
     * @param writer Writer to output this file to
     */
    public void save(Writer writer) {
        yaml.dump(saveMap(), writer);
    }

    /**
     * Returns the name of a plugin
     *
     * @return String name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the version of a plugin
     *
     * @return String name
     */
    public String getVersion() {
        return version;
    }

    /**
     * Returns the name of a plugin including the version
     *
     * @return String name
     */
    public String getFullName() {
        return name + " v" + version;
    }

    /**
     * Returns the main class for a plugin
     *
     * @return Java classpath
     */
    public String getMain() {
        return main;
    }

    public @Nullable Object getCommands() {
        return commands;
    }

    public List<String> getDepend() { // Poseidon - Object -> List<String>
        return depend;
    }

    public List<String> getSoftDepend() { // Poseidon - Object -> List<String>
        return softDepend;
    }

    // Poseidon start - backport loadbefore field
    public List<String> getLoadBefore() {
        return loadBefore;
    }
    // Poseidon end

    public PluginLoadOrder getLoad() {
        return order;
    }

    /**
     * Gets the description of this plugin
     *
     * return Description of this plugin
     */
    public @Nullable String getDescription() {
        return description;
    }

    // Poseidon start
    /**
     * Gets the token to prefix plugin-specific logging messages with
     *
     * @return the prefixed logging token
     */
    public @Nullable String getPrefix() {
        return prefix;
    }
    // Poseidon end

    public ArrayList<String> getAuthors() {
        return authors;
    }

    public @Nullable String getWebsite() {
        return website;
    }

    // Poseidon start - remove built-in database
    /*public boolean isDatabaseEnabled() {
        return database;
    }

    public void setDatabaseEnabled(boolean database) {
        this.database = database;
    }*/
    // Poseidon end

    public ArrayList<Permission> getPermissions() {
        return permissions;
    }

    private void loadMap(Map<String, Object> map) throws InvalidDescriptionException {
        try {
            name = map.get("name").toString();

            if (!name.matches("^[A-Za-z0-9 _.-]+$")) {
                throw new InvalidDescriptionException("name '" + name + "' contains invalid characters.");
            }
        } catch (NullPointerException ex) {
            throw new InvalidDescriptionException(ex, "name is not defined");
        } catch (ClassCastException ex) {
            throw new InvalidDescriptionException(ex, "name is of wrong type");
        }

        try {
            version = map.get("version").toString();
        } catch (NullPointerException ex) {
            throw new InvalidDescriptionException(ex, "version is not defined");
        } catch (ClassCastException ex) {
            throw new InvalidDescriptionException(ex, "version is of wrong type");
        }

        try {
            main = map.get("main").toString();
            if (main.startsWith("org.bukkit.")) {
                throw new InvalidDescriptionException("main may not be within the org.bukkit namespace");
            }
        } catch (NullPointerException ex) {
            throw new InvalidDescriptionException(ex, "main is not defined");
        } catch (ClassCastException ex) {
            throw new InvalidDescriptionException(ex, "main is of wrong type");
        }

        if (map.containsKey("commands")) {
            try {
                commands = map.get("commands");
            } catch (ClassCastException ex) {
                throw new InvalidDescriptionException(ex, "commands are of wrong type");
            }
        }

        if (map.containsKey("depend")) {
            try {
                depend = List.copyOf((List<String>) map.get("depend")); // Poseidon - immutable list
            } catch (ClassCastException ex) {
                throw new InvalidDescriptionException(ex, "depend is of wrong type");
            }
        }

        if (map.containsKey("softdepend")) {
            try {
                softDepend = List.copyOf((List<String>) map.get("softdepend")); // Poseidon - immutable list
            } catch (ClassCastException ex) {
                throw new InvalidDescriptionException(ex, "softdepend is of wrong type");
            }
        }

        // Poseidon start
        if (map.containsKey("loadbefore")) {
            try {
                loadBefore = List.copyOf((List<String>) map.get("loadbefore"));
            } catch (ClassCastException ex) {
                throw new InvalidDescriptionException(ex, "loadbefore is of wrong type");
            }
        }
        // Poseidon end

        // Poseidon start - remove built-in database
        /*if (map.containsKey("database")) {
            try {
                database = (Boolean) map.get("database");
            } catch (ClassCastException ex) {
                throw new InvalidDescriptionException(ex, "database is of wrong type");
            }
        }*/
        // Poseidon end

        if (map.containsKey("website")) {
            try {
                website = (String) map.get("website");
            } catch (ClassCastException ex) {
                throw new InvalidDescriptionException(ex, "website is of wrong type");
            }
        }

        if (map.containsKey("description")) {
            try {
                description = (String) map.get("description");
            } catch (ClassCastException ex) {
                throw new InvalidDescriptionException(ex, "description is of wrong type");
            }
        }

        // Poseidon start
        if (map.containsKey("prefix")) {
            try {
                prefix = (String) map.get("prefix");
            } catch (ClassCastException ex) {
                throw new InvalidDescriptionException(ex, "prefix is of wrong type");
            }
        }
        // Poseidon end

        if (map.containsKey("load")) {
            try {
                order = PluginLoadOrder.valueOf(((String)map.get("load")).toUpperCase().replaceAll("\\W", ""));
            } catch (ClassCastException ex) {
                throw new InvalidDescriptionException(ex, "load is of wrong type");
            } catch (IllegalArgumentException ex) {
                throw new InvalidDescriptionException(ex, "load is not a valid choice");
            }
        }

        if (map.containsKey("author")) {
            try {
                String extra = (String) map.get("author");

                authors.add(extra);
            } catch (ClassCastException ex) {
                throw new InvalidDescriptionException(ex, "author is of wrong type");
            }
        }

        if (map.containsKey("authors")) {
            try {
                ArrayList<String> extra = (ArrayList<String>) map.get("authors");

                authors.addAll(extra);
            } catch (ClassCastException ex) {
                throw new InvalidDescriptionException(ex, "authors are of wrong type");
            }
        }

        if (map.containsKey("permissions")) {
            try {
                 Map<String, Map<String, Object>> perms = (Map<String, Map<String, Object>>) map.get("permissions");

                 loadPermissions(perms);
            } catch (ClassCastException ex) {
                throw new InvalidDescriptionException(ex, "permissions are of wrong type");
            }
        }
    }

    private Map<String, Object> saveMap() {
        Map<String, Object> map = new HashMap<>();

        map.put("name", name);
        map.put("main", main);
        map.put("version", version);
        //map.put("database", database); // Poseidon - remove built-in database
        map.put("order", order.toString());

        if (commands != null) {
            map.put("command", commands);
        }
        if (depend != null) {
            map.put("depend", depend);
        }
        if (softDepend != null) {
            map.put("softdepend", softDepend);
        }
        // Poseidon start
        if (loadBefore != null) {
            map.put("loadbefore", loadBefore);
        }
        // Poseidon end
        if (website != null) {
            map.put("website", website);
        }
        if (description != null) {
            map.put("description", description);
        }
        // Poseidon start
        if (prefix != null) {
            map.put("prefix", prefix);
        }
        // Poseidon end

        if (authors.size() == 1) {
            map.put("author", authors.get(0));
        } else if (authors.size() > 1) {
            map.put("authors", authors);
        }

        return map;
    }

    private void loadPermissions(Map<String, Map<String, Object>> perms) {
        Set<String> keys = perms.keySet();

        for (String name : keys) {
            try {
                permissions.add(Permission.loadPermission(name, perms.get(name)));
            } catch (Throwable ex) {
                Bukkit.getServer().getLogger().log(Level.SEVERE, "Permission node '" + name + "' in plugin description file for " + getFullName() + " is invalid", ex);
            }
        }
    }
}
