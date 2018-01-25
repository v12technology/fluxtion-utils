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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Process {@link LogRecord}'s and publish to Log4J2. Will publish 
 * @author greg
 */
public class Log4JLogRecordListener implements LogRecordListener{

    private static Logger logger = LogManager.getLogger("fluxtion.eventLog");
    private static Level level = Level.INFO; 
    
    @Override
    public void processCalculationRecord(LogRecord logRecord) {
        logger.log(level, logRecord.asCharSequence());
        logger.log(level, "---");
    }
    
}
