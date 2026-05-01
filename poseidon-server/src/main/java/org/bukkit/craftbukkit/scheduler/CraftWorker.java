package org.bukkit.craftbukkit.scheduler;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitWorker;

import java.util.concurrent.ThreadFactory;

public class CraftWorker implements Runnable, BukkitWorker {

    private static int hashIdCounter = 1;
    private static Object hashIdCounterSync = new Object();

    private final int hashId;

    private final Plugin owner;
    private final int taskId;

    private final Thread t;
    private final CraftThreadManager parent;

    private final Runnable task;

    private static final ThreadFactory factory = Thread.ofPlatform().name("CraftWorker-", 1).factory(); // Poseidon

    CraftWorker(CraftThreadManager parent, Runnable task, Plugin owner, int taskId) {
        this.parent = parent;
        this.taskId = taskId;
        this.task = task;
        this.owner = owner;
        this.hashId = CraftWorker.getNextHashId();
        t = factory.newThread(this); // Poseidon
        t.start();
    }

    public void run() {

        try {
            task.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

        synchronized (parent.workers) {
            parent.workers.remove(this);
        }

    }

    public int getTaskId() {
        return taskId;
    }

    public Plugin getOwner() {
        return owner;
    }

    public Thread getThread() {
        return t;
    }

    public void interrupt() {
        t.interrupt();
    }

    public boolean isAlive() {
        return t.isAlive();
    }

    private static int getNextHashId() {
        synchronized (hashIdCounterSync) {
            return hashIdCounter++;
        }
    }

    @Override
    public int hashCode() {
        return hashId;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (!(other instanceof CraftWorker otherCraftWorker)) {
            return false;
        }

        return otherCraftWorker.hashCode() == hashId;
    }

}
