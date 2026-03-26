package net.minecraft.server;

import com.legacyminecraft.poseidon.logging.ServerLoggingConfigurator;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.ServiceLoader;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class ConsoleLogManager {

    public static Logger a = Logger.getLogger("Minecraft");

    public ConsoleLogManager() {}

    public static void init() {
        // Poseidon start - logging configuration
        a.setUseParentHandlers(true);
        Logger rootLogger = Logger.getLogger("");
        for (Handler handler : rootLogger.getHandlers()) {
            rootLogger.removeHandler(handler);
        }

        SLF4JBridgeHandler.install();
        ServiceLoader.load(ServerLoggingConfigurator.class);
        // Poseidon end
    }
}
