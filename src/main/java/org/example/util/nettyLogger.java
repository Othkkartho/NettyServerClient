package org.example.util;

import java.util.logging.Logger;

public class nettyLogger {
    private static Logger LOGGER = null;
    private static nettyLogger loggerInstance = null;
    private static final String TRAN_JNI_LOGGER_NAME = "tranNetty";

    private nettyLogger() {
        LOGGER = Logger.getLogger(TRAN_JNI_LOGGER_NAME);
    }

    public static nettyLogger getInstance() {
        if (loggerInstance == null) {
            loggerInstance = new nettyLogger();
        }

        return loggerInstance;
    }

    public Logger getLogConnection() {
        return LOGGER;
    }
}
