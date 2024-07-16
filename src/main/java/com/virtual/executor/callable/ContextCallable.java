package com.virtual.executor.callable;

import com.virtual.executor.context.ThreadDataContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.Callable;

import static java.lang.String.format;

public class ContextCallable<T> implements Callable<T> {

    private final Logger logger = LogManager.getLogger();

    private Callable<T> callable;

    private RequestAttributes requestAttributes;

    private ThreadDataContext threadDataContext;

    public ContextCallable(Callable<T> callable) {
        this.callable = callable;
    }

    public ContextCallable(Callable<T> callable, RequestAttributes requestAttributes) {
        this.callable = callable;
        this.requestAttributes = requestAttributes;
    }

    public ContextCallable(Callable<T> callable, RequestAttributes requestAttributes, ThreadDataContext threadDataContext) {
        this.callable = callable;
        this.requestAttributes = requestAttributes;
        this.threadDataContext = threadDataContext;
    }

    @Override
    public T call() throws Exception {
        try {
            ThreadDataContext threadDataContext = ThreadDataContext.getThreadDataContext();
            RequestContextHolder.setRequestAttributes(requestAttributes);
            ThreadDataContext.setThreadDataContext(threadDataContext);
            return callable.call();
        } finally {
            RequestContextHolder.resetRequestAttributes();
            if (!ObjectUtils.isEmpty(ThreadDataContext.getThreadDataContext()))
                ThreadDataContext.closeRequestThreadDataContext();
        }
    }

    private void doLogging(String message) {
        if (logger.isDebugEnabled())
            logger.debug(format("Debug message: %s", message));
    }
}
