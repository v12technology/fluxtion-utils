package com.fluxtion.runtime.plugin.logging;

import com.fluxtion.runtime.plugin.logging.EventLogConfig.LogLevel;

/**
 * A logger for an individual EventLoggerSource node. Users write double values
 * with keys using one of the convenience methods. The CalculationLogManager
 * will aggregate all data into a LogRecord and publish.
 *
 * @author Greg Higgins (greg.higgins@v12technology.com)
 */
public class EventLogger {

    private final LogRecord logrecord;
    private final String logSourceId;
    private LogLevel logLevel;

    public EventLogger(LogRecord logrecord, String logSourceId) {
        this.logrecord = logrecord;
        this.logSourceId = logSourceId;
        logLevel = LogLevel.INFO;
    }

    public void setLevel(LogLevel level) {
        logLevel = level;
    }

    public void info(String key, String value) {
        log(key, value, LogLevel.INFO);
    }

    public void debug(String key, String value) {
        log(key, value, LogLevel.DEBUG);
    }

    public void trace(String key, String value) {
        log(key, value, LogLevel.TRACE);
    }

    public void info(String key, boolean value) {
        log(key, value, LogLevel.INFO);
    }

    public void debug(String key, boolean value) {
        log(key, value, LogLevel.DEBUG);
    }

    public void trace(String key, boolean value) {
        log(key, value, LogLevel.TRACE);
    }

    public void info(String key, double value) {
        log(key, value, LogLevel.INFO);
    }

    public void debug(String key, double value) {
        log(key, value, LogLevel.DEBUG);
    }

    public void trace(String key, double value) {
        log(key, value, LogLevel.TRACE);
    }

    public void log(String key, double value, LogLevel logLevel) {
        if (this.logLevel.level >= logLevel.level) {
            logrecord.addRecord(logSourceId, key, value);
        }
    }

    public void log(String key, CharSequence value, LogLevel logLevel) {
        if (this.logLevel.level >= logLevel.level) {
            logrecord.addRecord(logSourceId, key, value);
        }
    }

    public void log(String key, boolean value, LogLevel logLevel) {
        if (this.logLevel.level >= logLevel.level) {
            logrecord.addRecord(logSourceId, key, value);
        }
    }
}
