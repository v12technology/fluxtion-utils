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

import com.fluxtion.runtime.event.Event;


/**
 * Control message to control the granularity of logging from a EventLogSource
 * source
 *
 * @author Greg Higgins (greg.higgins@v12technology.com)
 */
public class EventLogConfig extends Event {

    private LogLevel level = LogLevel.INFO;
    private String sourceId;
    private String groupId;

    public EventLogConfig() {
        this.level = LogLevel.INFO;
    }

    public EventLogConfig(String sourceId, String groupId, LogLevel level) {
        this.sourceId = sourceId;
        this.groupId = groupId;
        this.level = level;
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getGroupId() {
        return groupId;
    }
    
    public enum LogLevel {
        NONE(0), INFO(1), DEBUG(2), TRACE(3);

        private LogLevel(int level) {
            this.level = level;
        }
        public final int level;

    }

}
