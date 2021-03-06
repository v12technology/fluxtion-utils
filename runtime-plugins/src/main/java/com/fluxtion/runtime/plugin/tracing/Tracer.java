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
package com.fluxtion.runtime.plugin.tracing;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.runtime.audit.Auditor;
import com.fluxtion.runtime.plugin.auditing.DelegatingAuditor;
import com.fluxtion.runtime.plugin.events.ListenerRegistrationEvent;
import com.fluxtion.runtime.plugin.reflection.NodeDescription;
import com.fluxtion.runtime.plugin.tracing.TraceEvents.PublishProperties;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * A Tracer implements Auditor interface providing real-time property tracing
 * functionality. A field from any node in a SEP can be traced and published to
 * registered TraceRecordListeners.<p>
 *
 * Individual property traces are configured by sending a
 * {@link TracerConfigEvent} to the SEP with a Tracer built in.<p>
 *
 * Tracing can be on demand or on any event update:
 * <ul>
 * <li>demand: the user must send a {@link PublishProperties} event to trace and publish.
 * <li>event update: a trace will be published on any event processed.
 * </ul>
 * Traces are individually configured for each property, using a {@link TracerConfigEvent}.
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class Tracer implements Auditor {

    private HashMap<String, Object> name2Node;
    private HashMap<String, NodeDescription> name2NodeDescription;
    private Set<PropertyReader> onEventPropertyReaderSet;
    private Set<PropertyReader> allReaderSet;
    private Set<TraceRecordListener> listenerSet;

    @Override
    public void nodeRegistered(Object node, String nodeName) {
        name2Node.put(nodeName, node);
        name2NodeDescription.put(nodeName, NodeDescription.buildDescription(nodeName, node));
    }

    @EventHandler
    public void listenerUpdate(ListenerRegistrationEvent<TraceRecordListener> event) {
        TraceRecordListener listener = event.value;
        if (event.register) {
            listenerSet.add(listener);
        } else {
            listenerSet.remove(listener);
        }
    }

    public void addListener(TraceRecordListener listener) {
        listenerSet.add(listener);
    }

    public void removeListener(TraceRecordListener listener) {
        listenerSet.remove(listener);
    }

    @Override
    public void processingComplete() {
        onEventPropertyReaderSet.forEach(PropertyReader::recordValue);
        onEventPropertyReaderSet.forEach(p -> listenerSet.forEach(l -> l.update(p)));
    }

    @EventHandler
    public void publishProperties(PublishProperties publishProperties) {
        allReaderSet.forEach(PropertyReader::recordValue);
        onEventPropertyReaderSet.forEach(p -> listenerSet.forEach(l -> l.update(p)));
    }

    @EventHandler
    public void recorderControl(TracerConfigEvent propertyRecorderControl) {
        String nodeName = propertyRecorderControl.getNodeName();
        String fieldName = propertyRecorderControl.getFieldName();
        final PropertyReader propertyReader = new PropertyReader(nodeName, fieldName, name2Node.get(nodeName));
        if (propertyRecorderControl.isRecord()) {
            allReaderSet.add(propertyReader);
            if (!propertyRecorderControl.isPublishOnDemand()) {
                onEventPropertyReaderSet.add(propertyReader);
            }
        } else {
            onEventPropertyReaderSet.remove(propertyReader);
            allReaderSet.remove(propertyReader);
        }
    }

    public Collection<NodeDescription> getNodeDescription() {
        return Collections.unmodifiableCollection(name2NodeDescription.values());
    }

    //helper methods
    public Tracer addConsolePublisher() {
        removeConsolePublisher();
        listenerSet.add(new ConsoleListener());
        return this;
    }

    //TODO add http push to InfluxDb for Grafana graphing
    public Tracer addInfluxDbPublisher() {
        removeConsolePublisher();
        listenerSet.add(new ConsoleListener());
        return this;
    }

    public Tracer removeConsolePublisher() {
        for (Iterator<TraceRecordListener> iterator = listenerSet.iterator(); iterator.hasNext();) {
            TraceRecordListener listener = iterator.next();
            if (listener instanceof ConsoleListener) {
                iterator.remove();
            }
        }
        return this;
    }

    public Tracer addPropertyTrace(String nodeName, String fieldName, boolean publishOnDemand) {
        recorderControl(new TracerConfigEvent(nodeName, fieldName, true, publishOnDemand));
        return this;
    }

    public Tracer removePropertyTrace(String nodeName, String fieldName, boolean publishOnDemand) {
        recorderControl(new TracerConfigEvent(nodeName, fieldName, false, publishOnDemand));
        return this;
    }

    @Override
    public void init() {
        name2Node = new HashMap<>();
        name2NodeDescription = new HashMap<>();
        onEventPropertyReaderSet = new HashSet<>();
        allReaderSet = new HashSet<>();
        listenerSet = new HashSet<>();
    }

    public static class ConsoleListener implements TraceRecordListener {

        @Override
        public void update(TraceRecord propertyRecord) {
            System.out.println(propertyRecord.getInstanceName()
                    + "." + propertyRecord.getPropertyName()
                    + ": " + propertyRecord.getFormattedValue());
        }
    }

    public static Tracer addPropertyRecorder(com.fluxtion.runtime.lifecycle.EventHandler handler) {
        Tracer recorder = new Tracer();
        handler.onEvent(new DelegatingAuditor.AuditorRegistration(true, recorder));
        return recorder;
    }

    public enum ListenerTypes {
        CONSOLE, INFLUX
    }

}
