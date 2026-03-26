package net.minecraft.server;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;

public class ThreadCommandReader extends Thread {

    final MinecraftServer server;

    public ThreadCommandReader(MinecraftServer minecraftserver) {
        this.server = minecraftserver;
        // Poseidon start
        setName("Server console handler");
        setPriority(Thread.MIN_PRIORITY);
        setDaemon(true);
        // Poseidon end
    }

    public void run() {
        LineReader bufferedreader = MinecraftServer.reader; // Poseidon
        String s;

        try {
            // CraftBukkit start - JLine disabling compatibility
            while (!this.server.isStopped && MinecraftServer.isRunning(this.server)) {
                try {
                    if (org.bukkit.craftbukkit.Main.useJline) {
                        s = bufferedreader.readLine("> "); // Poseidon
                    } else {
                        s = bufferedreader.readLine();
                    }
                    if (s != null && !s.isBlank()) { // Poseidon - only handle non-empty input
                        this.server.issueCommand(s, this.server);
                    }
                    // CraftBukkit end
                } catch (EndOfFileException e) { // Poseidon
                }
            }
        } catch (UserInterruptException e) {
            this.server.a(); // Poseidon - shut down gracefully
        }
    }
}
