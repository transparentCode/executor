package com.virtual.executor.runnable;

import com.virtual.executor.context.ThreadDataContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class ContextRunnable implements Runnable {

    private final Logger logger = LogManager.getLogger();

    private RequestAttributes requestAttributes;

    private ThreadDataContext threadDataContext;

    private Runnable runnable;

    public ContextRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public ContextRunnable(Runnable runnable, RequestAttributes requestAttributes) {
        this.requestAttributes = requestAttributes;
        this.runnable = runnable;
    }

    public ContextRunnable(Runnable runnable, RequestAttributes requestAttributes, ThreadDataContext threadDataContext) {
        this.requestAttributes = requestAttributes;
        this.runnable = runnable;
        this.threadDataContext = threadDataContext;
    }

    @Override
    public void run() {
        try {
            ThreadDataContext threadDataContext = ThreadDataContext.getThreadDataContext();
            RequestContextHolder.setRequestAttributes(requestAttributes);
            ThreadDataContext.setThreadDataContext(threadDataContext);
            runnable.run();
        } finally {
            RequestContextHolder.resetRequestAttributes();
            if (!ObjectUtils.isEmpty(ThreadDataContext.getThreadDataContext()))
                ThreadDataContext.closeRequestThreadDataContext();
        }
    }
}
