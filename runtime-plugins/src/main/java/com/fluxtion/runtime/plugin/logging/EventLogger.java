/* 
 * Copyright (C) 2017 V12 Technology Limited (greg.higgins@v12technology.com)
 *
 * This file is part of Fluxtion.
 *
 * Fluxtion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
