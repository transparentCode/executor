package com.virtual.executor;

import com.virtual.executor.callable.ContextCallable;
import com.virtual.executor.factory.VirtualThreadFactory;
import com.virtual.executor.runnable.ContextRunnable;
import jakarta.annotation.PreDestroy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.virtual.executor.context.ThreadDataContext.getThreadDataContext;
import static org.springframework.web.context.request.RequestContextHolder.getRequestAttributes;

@Component
public class VirtualThreadExecutor {

    private final Logger logger = LogManager.getLogger();

    private final VirtualThreadFactory threadFactory = new VirtualThreadFactory(
            "WorkerThread-",
            true,
            Thread.NORM_PRIORITY
    );

    private final ExecutorService executor = Executors.newThreadPerTaskExecutor(threadFactory);

    public <T> Future<T> submitTask(Callable<T> task) {
        try {
            if (ObjectUtils.isEmpty(getRequestAttributes()))
                return executor.submit(new ContextCallable<>(task));
            return executor.submit(new ContextCallable<T>(
                    task,
                    getRequestAttributes(),
                    getThreadDataContext())
            );
        } finally {
            doLogging();
        }
    }

    public void submitTask(Runnable task) {
        try {
            if (ObjectUtils.isEmpty(getRequestAttributes()))
                executor.submit(new ContextRunnable(task));
            else
                executor.submit(new ContextRunnable(
                        task,
                        getRequestAttributes(),
                        getThreadDataContext())
                );
        } finally {
            doLogging();
        }
    }

    private void doLogging() {
        if (logger.isDebugEnabled())
            logger.info("Task executed");
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

