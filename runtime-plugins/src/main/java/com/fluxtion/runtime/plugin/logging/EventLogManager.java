package com.fluxtion.runtime.plugin.logging;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.runtime.audit.Auditor;
import com.fluxtion.runtime.event.Event;
import java.util.ArrayList;
import java.util.List;
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

    public List<EventLogSource> sources;
    public List<String> nodeNames;
    private LogRecordListener sink;
    private LogRecord logRecord;
    private Map<String, EventLogger> node2Logger;
    private boolean clearAfterPublish;

    public EventLogManager() {
        this.nodeNames = new ArrayList<>();
        this.sources = new ArrayList<>();
    }

    @Override
    public void nodeRegistered(Object node, String nodeName) {
        if (node instanceof EventLogSource) {
            sources.add((EventLogSource) node);
            nodeNames.add(nodeName);
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
        //for each source initialise 
        logRecord = new LogRecord();
        node2Logger = new HashMap<>();
        clearAfterPublish = true;
        for (int i = 0; i < sources.size(); i++) {
            EventLogSource calcSource = sources.get(i);
            String name = nodeNames.get(i);
            EventLogger logger = new EventLogger(logRecord, name);
            calcSource.setLogger(logger);
            node2Logger.put(name, logger);
        }
        sink = System.out::println;
    }

    @Override
    public void eventReceived(Event triggerEvent) {
        logRecord.triggerEvent(triggerEvent.getClass());
    }
    
    @Override
    public void eventReceived(Object triggerEvent) {
        logRecord.triggerObject(triggerEvent.getClass());
    }

}
