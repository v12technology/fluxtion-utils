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
package com.fluxtion.runtime.plugin.sep;

import com.fluxtion.api.node.SEPConfig;
import com.fluxtion.runtime.plugin.auditing.DelegatingAuditor;
import com.fluxtion.runtime.plugin.logging.EventLogManager;
import com.fluxtion.runtime.plugin.profiler.HdrProfiler;
import com.fluxtion.runtime.plugin.tracing.Tracer;

/**
 * A SEPConfig that has the following Auditors registered:
 * 
 * <ul>
 * <li>EventLogManager - nodeName: logger
 * <li>Tracer - nodeName: propertyTracer
 * <li>HdrProfiler - nodeName: profiler
 * <li>DelegatingAuditor - nodeName: delegatingAuditor
 * </ul>
 * 
 * @author V12 Technology Limited
 */
public class AuditedSep extends SEPConfig {

    public AuditedSep() {
        //add default auditors
        addAuditor(new EventLogManager(), "logger");
        addAuditor(new Tracer(), "propertyTracer");
        addAuditor(new HdrProfiler(), "profiler");
        addAuditor(new DelegatingAuditor(), "delegatingAuditor");
    }

}
