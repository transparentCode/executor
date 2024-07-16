package com.virtual.executor.context;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serial;
import java.io.Serializable;

public class ThreadDataContext implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger();

    private static final ThreadLocal<ThreadDataContext> threadData = new ThreadLocal<>();

    public static ThreadDataContext getThreadDataContext() {
        return threadData.get();
    }

    public static void setThreadDataContext(ThreadDataContext dataContext) {
        threadData.set(dataContext);
    }

    public static void closeRequestThreadDataContext() {
        threadData.remove();
        doLogging();
    }

    public static ThreadDataContext createThreadDataContext() {
        threadData.set(new ThreadDataContext());
        return threadData.get();
    }

    private static void doLogging() {
        if (logger.isDebugEnabled())
            logger.debug("****** Thread data removed ******");
    }

}
