/*
 * Copyright (C) 2017 Greg Higgins (greg.higgins@v12technology.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.fluxtion.runtime.plugin.logging;

import com.fluxtion.runtime.event.Event;

/**
 * A log record from a double producing source. This is a structured log record
 * that can be easily converted to a long term store, such as a rdbms for later
 * analysis.
 *
 * @author Greg Higgins (greg.higgins@v12technology.com)
 */
public class LogRecord {

    /**
     * The id of the instance producing the record. GroupingId can be used to
     * group LogRecord's together.
     *
     */
    public String groupingId;

    
    public long eventId;
    /**
     * The time the event was received
     */
    public long eventTime;
    /**
     * The time the log was processed.
     */
    public long logTime;

    private final StringBuilder sb;

    private String sourceId;

    private boolean firstProp;

    public LogRecord() {
        sb = new StringBuilder();
        firstProp = true;
    }

    public void addRecord(String sourceId, String propertyKey, double value) {
        addSourceId(sourceId, propertyKey);
        if (value % 1 == 0) {
            sb.append((int) value);
        } else {
            sb.append(value);
        }
    }

    public void addRecord(String sourceId, String propertyKey, CharSequence value) {
        addSourceId(sourceId, propertyKey);
        sb.append(value);
    }

    public void addRecord(String sourceId, String propertyKey, boolean value) {
        addSourceId(sourceId, propertyKey);
        sb.append(value);
    }

    private void addSourceId(String sourceId, String propertyKey) {
        if (this.sourceId == null) {
            sb.append("\n\t\t").append(sourceId).append(": {");
            this.sourceId = sourceId;
        } else if (!this.sourceId.equals(sourceId)) {
            sb.append("},\n\t\t").append(sourceId).append(": {");
            this.sourceId = sourceId;
            firstProp = true;
        }
        if (!firstProp) {
            sb.append(",");
        }
        firstProp = false;
        sb.append(" ").append(propertyKey).append(": ");
    }

    public void clear() {
        firstProp = true;
        sourceId = null;
        sb.setLength(0);
    }

    public CharSequence asCharSequence() {
        return sb;
    }

    public void triggerEvent(Class<? extends Event> aClass) {
        sb.append("eventLogRecord: {");
        sb.append("\n\tlogTime: ").append(System.currentTimeMillis()).append(',');
        sb.append("\n\tgroupingId: ").append(groupingId).append(',');
        sb.append("\n\tevent: ").append(aClass.getSimpleName()).append(',');
        sb.append("\n\tnodeLogs: [");
    }

    public void terminateRecord() {
        if (this.sourceId != null) {
            sb.append("}");
        }
        sb.append("\n\t]");
        sb.append("\n}");
        firstProp = true;
        sourceId = null;
    }

    @Override
    public String toString() {
        return asCharSequence().toString();
    }

}
