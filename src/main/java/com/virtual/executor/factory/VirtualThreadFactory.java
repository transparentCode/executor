package com.virtual.executor.factory;

import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class VirtualThreadFactory implements ThreadFactory {

    private static final Logger logger = LogManager.getLogger();

    private final String namePrefix;
    private final boolean daemon;
    private final int priority;
//    private final MeterRegistry registry;
    public static final AtomicInteger runningThreads = new AtomicInteger(0);
    public static final AtomicInteger blockedThreads = new AtomicInteger(0);
    public static final AtomicInteger waitingThreads = new AtomicInteger(0);

    /**
     * @param namePrefix - workerThreads prefix name
     * @param daemon - set the daemon (default virtual threads are daemon threads i.e flag set to true)
     * @param priority - workerThreads priority
     */
    public VirtualThreadFactory(String namePrefix, boolean daemon, int priority) {
        this.namePrefix = namePrefix + " " + runningThreads.get();
        this.daemon = daemon;
        this.priority = priority;
//        this.registry = registry;
//
//        registry.gauge("threads.virtual.running", runningThreads);
//        registry.gauge("threads.virtual.blocked", blockedThreads);
//        registry.gauge("threads.virtual.waiting", waitingThreads);
    }

    @Override
    public Thread newThread(@NonNull Runnable r) {
        Thread thread = buildWorkerThread(getRunnable(r));
        runningThreads.incrementAndGet();
        updateThreadState(thread);
        return thread;
    }

    private Thread buildWorkerThread(@NonNull Runnable runnable) {
        var thread = Thread.ofVirtual().unstarted(runnable);
        thread.setName(namePrefix + "-" + thread.threadId());
        thread.setDaemon(daemon);
        thread.setPriority(priority);
        return thread;
    }

    private Runnable getRunnable(@NonNull Runnable r) {
        return () -> {
            Thread currentThread = Thread.currentThread();
            doLogging(currentThread, "Starting");
            try {
                r.run();
            } finally {
                runningThreads.decrementAndGet();
                updateThreadState(currentThread);
                doLogging(currentThread, "Finished");
            }
        };
    }

    private void updateThreadState(Thread thread) {
        Thread.State state = thread.getState();
        switch (state) {
            case BLOCKED:
                blockedThreads.incrementAndGet();
                break;
            case WAITING:
            case TIMED_WAITING:
                waitingThreads.incrementAndGet();
                break;
            default:
                break;
        }
    }

    private void doLogging(Thread thread, String state) {
        if (logger.isDebugEnabled())
            logger.info("Thread {} - {}, State: {}", thread.getName(), state, thread.getState());
    }
}
