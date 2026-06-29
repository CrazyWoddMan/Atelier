package crazywoddman.atelier.api;

import java.util.LinkedHashSet;
import java.util.Set;

import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.fml.LogicalSide;

public final class Queue {
    public static final Queue
    SERVER = new Queue(),
    CLIENT = new Queue();
    public static final int NO_ID = 0;
    private static int id = 0;

    private final Set<DelayedRunnable> tasks = new LinkedHashSet<>();

    private Queue() {}

    public boolean hasTask(int id) {
        for (DelayedRunnable task : this.tasks)
            if (task.id == id)
                return true;

        return false;
    }

    /**
     * @param id unique id. Use {@link Queue#makeID} to create such id
     * @param delay tick delay
     * @param task the task to run after {@code delay} ticks
     * @return {@code false} if the queue already contains task with such id
     */
    public boolean add(int id, int delay, Runnable task) {
        return this.tasks.add(new DelayedRunnable(task, id, delay));
    }

    /**
     * @param delay tick delay
     * @param task the task to run after {@code delay} ticks
     */
    public void add(int delay, Runnable task) {
        add(NO_ID, delay, task);
    }

    public void tick() {
        for (DelayedRunnable task : this.tasks) {
            if (task.delay-- <= 0) {
                this.tasks.remove(task);
                task.runnable.run();
            }
        }
    }

    /** For internal use only */
    public void clear() {
        this.tasks.clear();
    }

    public static Queue of(LevelAccessor level) {
        return level.isClientSide() ? CLIENT : SERVER;
    }

    public static Queue of(LogicalSide side) {
        return switch (side) {
            case CLIENT -> CLIENT;
            case SERVER -> SERVER;
        };
    }

    public static int makeID() {
        return ++id;
    }

    private static class DelayedRunnable {
        final Runnable runnable;
        final int id;
        int delay;

        DelayedRunnable(Runnable runnable, int id, int delay) {
            this.runnable = runnable;
            this.id = id;
            this.delay = delay;
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj) || (this.id != NO_ID && obj instanceof DelayedRunnable task && this.id == task.id);
        }
    }
}
