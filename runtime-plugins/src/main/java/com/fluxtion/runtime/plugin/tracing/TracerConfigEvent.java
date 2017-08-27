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

    private final String nodeName;
    private final String fieldName;
    private final boolean record;
    private final boolean publishOnDemand;

    public TracerConfigEvent(String nodeName, String fieldName, boolean record, boolean publishOnDemand) {
        this.nodeName = nodeName;
        this.fieldName = fieldName;
        this.record = record;
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
    
}
