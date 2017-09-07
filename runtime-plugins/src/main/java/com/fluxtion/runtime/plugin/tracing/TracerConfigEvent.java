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

import com.fluxtion.runtime.event.Event;

/**
 * An event to configure property tracing for a {@link Tracer} registered in a static event processor.
 * The {@link #publishOnDemand} property controls when properties are traced and
 * published:
 *
 * <ul>
 * <li>publishOnDemand:true - the user must send a {@link PublishProperties} event to trace and publish. This is the default setting
 * <li>publishOnDemand:false - a trace will be published on any event processed. 
 * </ul>
 * 
 * The {@link #record} property controls if a property is traced:
 *
 * <ul>
 * <li>record:true - add a trace
 * <li>record:false - remove a trace
 * </ul>
 *
 * 
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class TracerConfigEvent extends Event {

    /**
     * The node name to extract properties from
     */
    private String nodeName;
    /**
     * The field name to extract value from
     */
    private String fieldName;
    /**
     * Add or remove trace - false = remove trace
     */
    private boolean record;
    /**
     * publish trace record on every event or on demand, using PublishProperties
     * event
     */
    private boolean publishOnDemand;

    public TracerConfigEvent(String nodeName, String fieldName, boolean record, boolean publishOnDemand) {
        this.nodeName = nodeName;
        this.fieldName = fieldName;
        this.record = record;
        this.publishOnDemand = publishOnDemand;
    }

    public TracerConfigEvent(String nodeName, String fieldName, boolean record) {
        this.nodeName = nodeName;
        this.fieldName = fieldName;
        this.record = record;
        this.publishOnDemand = true;
    }
    public TracerConfigEvent(String nodeName, String fieldName) {
        this.nodeName = nodeName;
        this.fieldName = fieldName;
        this.record = true;
        this.publishOnDemand = true;
    }

    public TracerConfigEvent() {
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setRecord(boolean record) {
        this.record = record;
    }

    public void setPublishOnDemand(boolean publishOnDemand) {
        this.publishOnDemand = publishOnDemand;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public boolean isRecord() {
        return record;
    }

    public boolean isPublishOnDemand() {
        return publishOnDemand;
    }

    @Override
    public String toString() {
        return "TracerConfigEvent{" + "nodeName=" + nodeName + ", fieldName=" + fieldName + ", record=" + record + ", publishOnDemand=" + publishOnDemand + '}';
    }

}
