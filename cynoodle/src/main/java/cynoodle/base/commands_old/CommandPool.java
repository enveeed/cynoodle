/*
 * cynoodle, a bot for the chat platform Discord
 *
 * Copyright (C) 2019 enveeed
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * All trademarks are the property of their respective owners, including, but not limited to Discord Inc.
 */

package cynoodle.base.commands;

import com.google.common.flogger.FluentLogger;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Dynamic execution pool for commands.
 */
public final class CommandPool {

    private final static FluentLogger LOG = FluentLogger.forEnclosingClass();

    // ===

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
            try {
                command.execute(this.context);
            } catch (Exception e) {
                LOG.atSevere()
                        .withCause(e)
                        .log("Unexpected internal exception during command.execute() pipeline!");
            }
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
