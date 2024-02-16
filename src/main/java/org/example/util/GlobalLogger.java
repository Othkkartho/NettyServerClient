package org.example.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GlobalLogger {
    private Logger logger;

    public GlobalLogger(String name) {
        logger = Logger.getLogger(name);
    }

    public void logging(Level level, String message) {
        logger.log(level, message);
    }
}
