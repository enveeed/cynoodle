/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.base.commands;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Dynamic execution pool for commands.
 */
public final class CommandPool {

    private final static int DEF_CORE_POOL_SIZE = 32;
    private final static int DEF_MAX_POOL_SIZE = 64;
    private final static long DEF_KEEP_ALIVE_TIME = 500; // milliseconds

    // ===

    /**
     * The queue holding all tasks in the executor.
     */
    private final PriorityBlockingQueue<Runnable> queue = new PriorityBlockingQueue<>(16);

    // ===

    /**
     * Thread ID counter, assigns a unique ID to each new thread in the executor pool (used in the thread name).
     */
    private final AtomicLong threadIDCounter = new AtomicLong(0);

    /**
     * Thread factory for the executor, creates new threads and assigns the required properties.
     */
    private final ThreadFactory threadFactory = r -> {
        Thread thread = new Thread(r);
        thread.setName("command-" + threadIDCounter.getAndIncrement());
        thread.setDaemon(false);
        return thread;
    };

    // ===

    /**
     * The executor for all command tasks.
     */
    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(DEF_CORE_POOL_SIZE, threadFactory); // TODO fix this

    // ===

    public void setCorePoolSize(int size) {
        this.executor.setCorePoolSize(size);
    }

    public void setMaximumPoolSize(int size) {
        this.executor.setMaximumPoolSize(size);
    }

    public void setKeepAliveTime(long time) {
        this.executor.setKeepAliveTime(time, TimeUnit.MILLISECONDS);
    }

    // ===

    /**
     * Submit a command for execution.
     * @param command the command to execute
     * @param context the context for this execution.
     */
    void submit(@Nonnull Command command, @Nonnull CommandContext context) {
        this.executor.submit(new CommandTask(command, context));
    }

    private final class CommandTask implements Callable<Void>, Comparable<CommandTask> {

        private final Command command;
        private final CommandContext context;

        // ===

        private CommandTask(Command command, CommandContext context) {
            this.command = command;
            this.context = context;
        }

        // ===

        @Override
        public Void call() throws Exception {
            command.execute(this.context);
            return null;
        }

        //

        @Override
        public int compareTo(@NotNull CommandPool.CommandTask o) {
            return 0;
        }
    }

    // ===

    /**
     * Shutdown the pool, awaiting termination of the execution of already submitted commands.
     */
    void shutdown() {

        this.executor.shutdown();

        try {
            this.executor.awaitTermination(10000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace(); // TODO report,etc
        }
    }

}
