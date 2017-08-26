/* 
 * Copyright (C) 2017 V12 Technology Limited
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
package com.fluxtion.runtime.plugin.tracing;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.runtime.audit.Auditor;
import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.plugin.auditing.DelegatingAuditor;
import com.fluxtion.runtime.plugin.events.ClassFilterEvent;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class Tracer implements Auditor {

    private HashMap<String, Object> name2Node;
    private Set<PropertyReader> onEventPropertyReaderSet;
    private Set<PropertyReader> allReaderSet;
    private Set<TraceRecordListener> listenerSet;

    @Override
    public void nodeRegistered(Object node, String nodeName) {
        name2Node.put(nodeName, node);
    }

    @EventHandler(filterStringFromClass = Tracer.class)
    public void listenerUpdate(ClassFilterEvent event) {
        TraceRecordListener listener = (TraceRecordListener) event.getListener();
//        listenerUpdate(listener);
        if (event.isRegister()) {
            listenerSet.add(listener);
        } else {
            listenerSet.remove(listener);
        }
    }

    public void listenerUpdate(ListenerUpdate event) {
        TraceRecordListener listener = (TraceRecordListener) event.listener;
        if (event.add) {
            listenerSet.add(listener);
        } else {
            listenerSet.remove(listener);
        }
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

    //helper methods
    public Tracer addConsolePublisher() {
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
        onEventPropertyReaderSet = new HashSet<>();
        allReaderSet = new HashSet<>();
        listenerSet = new HashSet<>();
    }

    public static class PublishProperties extends Event {
    }

    public static class ListenerUpdate extends Event {

        private final boolean add;
        private final TraceRecordListener listener;

        public ListenerUpdate(boolean add, TraceRecordListener listener) {
            this.add = add;
            this.listener = listener;
        }

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

    public static class MyConfig<T> {

        T config;
    }

    public void myHandler(MyConfig<? extends String> strConfonfig) {

    }

    public static void main(String[] args) throws NoSuchMethodException {
        Method declaredMethod = Tracer.class.getDeclaredMethod("myHandler", MyConfig.class);
        ParameterizedType pt = (ParameterizedType) declaredMethod.getGenericParameterTypes()[0];
        final Type actualType = pt.getActualTypeArguments()[0];
        final Class rawType = (Class) pt.getRawType();
        System.out.println("parameter type:" + actualType);
        System.out.println("raw type:" + rawType);
        System.out.printf("cast: (%s<%s>)%n", rawType.getSimpleName(), actualType);
    }

}
