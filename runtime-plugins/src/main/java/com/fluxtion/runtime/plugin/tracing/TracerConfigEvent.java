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
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class TracerConfigEvent extends Event {

    private String nodeName;
    private String fieldName;
    private boolean record;
    private boolean publishOnDemand;

    public TracerConfigEvent(String nodeName, String fieldName, boolean record, boolean publishOnDemand) {
        this.nodeName = nodeName;
        this.fieldName = fieldName;
        this.record = record;
        this.publishOnDemand = publishOnDemand;
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
