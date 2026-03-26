package org.bukkit.craftbukkit;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerOutputStream extends ByteArrayOutputStream {

    private final String separator = System.lineSeparator(); // Poseidon - System.lineSeparator()
    private final Logger logger;
    private final Level level;

    public LoggerOutputStream(Logger logger, Level level) {
        this.logger = logger;
        this.level = level;
    }

    @Override
    public synchronized void flush() {
        String record = this.toString(StandardCharsets.UTF_8); // Poseidon
        reset();

        // Poseidon start - fix duplicate newlines when using System.out.println()
        if (!record.isEmpty() && record.charAt(record.length() - 1) == '\n') {
            record = record.substring(0, record.length() - 1);
        }
        // Poseidon end

        if (!record.isEmpty() && !record.equals(this.separator)) {
            this.logger.logp(this.level, "", "", record);
        }
    }
}
