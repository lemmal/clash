package com.clash.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClashLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClashLogger.class);

    public static void debug(String msg, Object... arguments) {
        LOGGER.debug(msg, arguments);
    }

    public static void info(String msg, Object... arguments) {
        LOGGER.info(msg, arguments);
    }

    public static void info(String msg, Throwable t) {
        LOGGER.info(msg, t);
    }

    public static void error(String msg, Object... arguments) {
        LOGGER.error(msg, arguments);
    }

    public static void error(String msg, Throwable t) {
        LOGGER.error(msg, t);
    }
}
