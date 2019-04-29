/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.concurrent;

import com.google.common.flogger.FluentLogger;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A wrapper for a self-repeating task according to a specific {@link Schedule}.
 */
public final class Service {

    private final static FluentLogger LOG = FluentLogger.forEnclosingClass();

    // ===

    private final Callable<Void> task;
    private final Schedule schedule;

    private final ScheduledExecutorService executor;

    // ===

    private final ServiceTask internal = new ServiceTask();

    // ===

    private Lock lock = new ReentrantLock();
    private Condition notWorking = lock.newCondition();
    private Condition working = lock.newCondition();

    // ===

    private Service(@Nonnull Callable<Void> task,
                    @Nonnull Schedule schedule,
                    @Nonnull ScheduledExecutorService executor) {
        this.task = task;
        this.schedule = schedule;
        this.executor = executor;
    }

    // ===

    @Nonnull
    public Schedule getSchedule() {
        return this.schedule;
    }

    // ===

    public void start() {
        this.internal.activate();
    }

    public void stop() {
        this.internal.deactivate();
        this.internal.interrupt();
    }

    //

    public void awaitStart() {
        lock.lock();
        try {
            while (!this.internal.isWorking()) working.awaitUninterruptibly();
        } finally {
            lock.unlock();
        }
    }

    public void awaitStop() {
        lock.lock();
        try {
            while (this.internal.isWorking()) notWorking.awaitUninterruptibly();
        } finally {
            lock.unlock();
        }
    }

    //

    private void signalNotWorking() {
        lock.lock();
        try {
            notWorking.signal();
        } finally {
            lock.unlock();
        }
    }

    private void signalWorking() {
        lock.lock();
        try {
            working.signal();
        } finally {
            lock.unlock();
        }
    }

    //

    public boolean isActive() {
        return this.internal.isActive();
    }

    // ===

    private final class ServiceTask implements Callable<Void> {

        boolean active = false;
        boolean working = false;

        // ===

        private ScheduledFuture<Void> future = null; // always the future of the LAST or CURRENT run

        // ===

        /**
         * Calculate the delay in milliseconds until the next
         * execution of the task.
         * @return the delay in milliseconds, from now
         */
        private long calculateDelay() {

            Instant next = schedule.next();
            Instant now = Instant.now();

            if(next.isBefore(now)) return 0L;
            else return Duration.between(now,next).toMillis();
        }

        //

        /**
         * Execute the service task.
         * Waits until the point the task was scheduled to is reached,
         * then executes the task.
         * @return null, always
         * @throws Exception if the task threw an exception
         */
        @Override
        public Void call() throws Exception {
            synchronized (this) {

                try {
                    task.call();
                } catch (InterruptedException interrupt) {
                    return null;
                } finally {

                    working = false;
                    signalNotWorking();

                    if(active) submit(); // re-submit, if not re-submitted, so the service died

                }

                return null;

            }
        }

        //

        /**
         * Submit the service task into the pool.
         */
        void submit() {
            synchronized (this) {
                this.working = true;
                signalWorking();
                this.future = executor.schedule(this, calculateDelay(), TimeUnit.MILLISECONDS);
            }
        }

        //

        /**
         * Notify the service task to start.
         */
        void activate() {

            if(!active) {

                active = true;

                // task is set to inactive
                if(working) {
                    // there is one in the queue / executing, so we can just activate again
                    // and it will auto re-submit
                }
                else {
                    // no task in queue / executing
                    submit();
                }
            }
            else {
                // task is active
                if(working) {
                    // running normally
                }
                else {
                    // somehow no task in queue or executing despite being active, re-submit
                    submit();
                }
            }


        }

        /**
         * Notify the service task to halt.
         */
        void deactivate() {

            if(active) {

                active = false;

                if(working) {
                    // it will die after the work
                }
                else {
                    // it already died
                }

            }
            else {
                // already inactive
                if(working) {
                    // it will die after the work
                }
                else {
                    // everything is fine
                }
            }

        }

        /**
         * Interrupt the underlying thread if running,
         * causing the task iteration to complete earlier.
         * Affects sleep AND execution.
         */
        void interrupt() {

            if(future != null) {
                // if there is no future then there is nothing to interrupt
                future.cancel(true);
                working = false;
                signalNotWorking();
            }
        }

        //

        /**
         * Check if the service is marked as active, meaning
         * that it's supposed to run or not
         * @return true if active, false if not
         */
        public boolean isActive() {
            return active;
        }

        //

        /**
         * Check if the service is currently queued in the pool or executing.
         * Only false if the service died or wasn't re-submitted yet.
         * @return true if in queue or executing, false if not
         */
        public boolean isWorking() {
            return working;
        }
    }

    // ===

    @Nonnull
    public static Service of(@Nonnull Callable<Void> task,
                             @Nonnull Schedule schedule,
                             @Nonnull ScheduledExecutorService executor) {
        return new Service(task, schedule, executor);
    }

    @Nonnull
    public static Service of(@Nonnull Runnable task,
                             @Nonnull Schedule schedule,
                             @Nonnull ScheduledExecutorService executor) {
        return new Service(() -> {
            task.run();
            return null;
        }, schedule, executor);
    }
}
