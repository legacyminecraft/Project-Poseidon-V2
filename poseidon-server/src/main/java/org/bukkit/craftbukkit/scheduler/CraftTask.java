package org.bukkit.craftbukkit.scheduler;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class CraftTask implements Comparable<Object>, BukkitTask {

    private final Runnable task;
    private final boolean syncTask;
    private long executionTick;
    private final long period;
    private final Plugin owner;
    private final int idNumber;

    private static Integer idCounter = 1;
    private static Object idCounterSync = new Object();

    CraftTask(Plugin owner, Runnable task, boolean syncTask) {
        this(owner, task, syncTask, -1, -1);
    }

    CraftTask(Plugin owner, Runnable task, boolean syncTask, long executionTick) {
        this(owner, task, syncTask, executionTick, -1);
    }

    CraftTask(Plugin owner, Runnable task, boolean syncTask, long executionTick, long period) {
        this.task = task;
        this.syncTask = syncTask;
        this.executionTick = executionTick;
        this.period = period;
        this.owner = owner;
        this.idNumber = CraftTask.getNextId();
    }

    static int getNextId() {
        synchronized (idCounterSync) {
            idCounter++;
            return idCounter;
        }
    }

    Runnable getTask() {
        return task;
    }

    public boolean isSync() {
        return syncTask;
    }

    long getExecutionTick() {
        return executionTick;
    }

    long getPeriod() {
        return period;
    }

    public Plugin getOwner() {
        return owner;
    }

    void updateExecution() {
        executionTick += period;
    }

    public int getTaskId() {
        return getIdNumber();
    }

    int getIdNumber() {
        return idNumber;
    }

    public int compareTo(Object other) {
        if (!(other instanceof CraftTask o)) {
            return 0;
        } else {
            long timeDiff = executionTick - o.getExecutionTick();
            if (timeDiff > 0) {
                return 1;
            } else if (timeDiff < 0) {
                return -1;
            } else {
                return getIdNumber() - o.getIdNumber();
            }
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (!(other instanceof CraftTask otherCraftTask)) {
            return false;
        }

        return otherCraftTask.getIdNumber() == getIdNumber();
    }

    @Override
    public int hashCode() {
        return getIdNumber();
    }
}
