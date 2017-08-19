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
