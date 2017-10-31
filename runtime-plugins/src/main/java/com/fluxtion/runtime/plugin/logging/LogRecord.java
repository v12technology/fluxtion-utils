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
 * This is a structured log record that can be easily converted to a long term
 * store, such as a rdbms for later analysis. The LogRecord creates a yaml
 * representation of the LogRecord for simplified marshaling.
 * 
 * A log record holds a set of 
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
            sb.append("\n        ").append(sourceId).append(": {");
            this.sourceId = sourceId;
        } else if (!this.sourceId.equals(sourceId)) {
            sb.append("},\n        ").append(sourceId).append(": {");
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

    public void triggerEvent(Event event) {
        Class<? extends Event> aClass = event.getClass();
        sb.append("eventLogRecord: {");
        sb.append("\n    logTime: ").append(System.currentTimeMillis()).append(',');
        sb.append("\n    groupingId: ").append(groupingId).append(',');
        sb.append("\n    event: ").append(aClass.getSimpleName()).append(',');
        if (event.filterString() != null && !event.filterString().isEmpty()) {
            sb.append("\n    eventFilter: ").append(event.filterString()).append(',');
        }
        sb.append("\n    nodeLogs: [");
    }

    public void triggerObject(Object event) {
        if (event instanceof Event) {
            triggerEvent((Event) event);
        } else {
            Class<?> aClass = event.getClass();
            sb.append("eventLogRecord: {");
            sb.append("\n    logTime: ").append(System.currentTimeMillis()).append(',');
            sb.append("\n    groupingId: ").append(groupingId).append(',');
            sb.append("\n    event: ").append(aClass.getSimpleName()).append(',');
            sb.append("\n    nodeLogs: [");
        }
    }

    /**
     * complete record processing, the return value indicates if any log values
     * were written.
     *
     * @return flag to indicate properties were logged
     */
    public boolean terminateRecord() {
        boolean logged = !firstProp;
        if (this.sourceId != null) {
            sb.append("}");
        }
        sb.append("\n    ]");
        sb.append("\n}");
        firstProp = true;
        sourceId = null;
        return logged;
    }

    @Override
    public String toString() {
        return asCharSequence().toString();
    }

}
