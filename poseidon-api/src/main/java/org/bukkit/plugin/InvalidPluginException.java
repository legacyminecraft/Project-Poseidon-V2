package org.bukkit.plugin;

import org.jspecify.annotations.Nullable;

/**
 * Thrown when attempting to load an invalid Plugin file
 */
public class InvalidPluginException extends Exception {
    private static final long serialVersionUID = -8242141640709409542L;
    private final @Nullable Throwable cause;

    /**
     * Constructs a new InvalidPluginException based on the given Exception
     *
     * @param throwable Exception that triggered this Exception
     */
    public InvalidPluginException(@Nullable Throwable throwable) {
        cause = throwable;
    }

    /**
     * Constructs a new InvalidPluginException
     */
    public InvalidPluginException() {
        cause = null;
    }

    /**
     * If applicable, returns the Exception that triggered this Exception
     *
     * @return Inner exception, or null if one does not exist
     */
    @Override
    public @Nullable Throwable getCause() {
        return cause;
    }
}
