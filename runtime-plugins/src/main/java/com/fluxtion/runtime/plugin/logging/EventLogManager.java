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

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.runtime.audit.Auditor;
import com.fluxtion.runtime.event.Event;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages and publishes a LogRecord to a LogRecordListener. The LogRecord is
 * hydrated from a list of EventLogSource's. The EventLogManager configures and
 * supplies a EventLogger for each registered EventLogSource. The output from
 * each EventLogSource is aggregated into the LogRecord and published.
 *
 * By default all data in the LogRecord is cleared after a publish. Clearing
 * behaviour is controlled with clearAfterPublish flag.
 *
 * EventLogConfig events set the processingComplete level for each registered
 * EventLogSource.
 *
 * @author Greg Higgins (greg.higgins@v12technology.com)
 */
public class EventLogManager implements Auditor {

    private LogRecordListener sink;
    private LogRecord logRecord;
    private Map<String, EventLogger> node2Logger;
    private boolean clearAfterPublish;

    @Override
    public void nodeRegistered(Object node, String nodeName) {
        if (node instanceof EventLogSource) {
            EventLogSource calcSource = (EventLogSource) node;
            EventLogger logger = new EventLogger(logRecord, nodeName);
            calcSource.setLogger(logger);
            node2Logger.put(nodeName, logger);
        }
    }

    @EventHandler(propagate = false)
    public void calculationLogConfig(EventLogConfig newConfig) {
        if (logRecord.groupingId != null && logRecord.groupingId.equals(newConfig.getGroupId())) {
            System.out.println("CalculationLogManager::updateLogConfig");
            node2Logger.computeIfPresent(newConfig.getSourceId(), (t, u) -> {
                u.setLevel(newConfig.getLevel());
                return u;
            });
            if (newConfig.getSourceId() == null) {
                node2Logger.values().forEach((t) -> t.setLevel(newConfig.getLevel()));
            }
        }
    }

    public void setLogSink(LogRecordListener sink) {
        this.sink = sink;
    }

    public void setLogGroupId(String groupId) {
        logRecord.groupingId = groupId;
    }

    public void setClearAfterPublish(boolean clearAfterPublish) {
        this.clearAfterPublish = clearAfterPublish;
    }

//    @AfterEvent
    @Override
    public void processingComplete() {
        logRecord.terminateRecord();
        sink.processCalculationRecord(logRecord);
        if (clearAfterPublish) {
            logRecord.clear();
        }
    }

//    @Initialise
    @Override
    public void init() {
        logRecord = new LogRecord();
        node2Logger = new HashMap<>();
        clearAfterPublish = true;
        sink = (l) -> {};
    }

    @Override
    public void eventReceived(Event triggerEvent) {
        logRecord.triggerEvent(triggerEvent);
    }

    @Override
    public void eventReceived(Object triggerEvent) {
        logRecord.triggerObject(triggerEvent);
    }

}
