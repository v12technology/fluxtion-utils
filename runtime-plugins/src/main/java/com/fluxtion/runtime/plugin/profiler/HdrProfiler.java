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
package com.fluxtion.runtime.plugin.profiler;

import com.fluxtion.runtime.audit.Auditor;
import com.fluxtion.runtime.event.Event;
import java.util.HashMap;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class HdrProfiler implements Auditor{

    
//    private HashMap<String, 
    
    @Override
    public void nodeRegistered(Object node, String nodeName) {
//        System.out.println("registered node:" + nodeName);
    }

    @Override
    public void eventReceived(Event event) {
//        System.out.println("processing event:" + event.getClass().getSimpleName());
    }

    @Override
    public void eventReceived(Object event) {
//        System.out.println("processing event:" + event.getClass().getSimpleName());
    }

    @Override
    public void processingComplete() {
//        System.out.println("processingComplete");
    }

    @Override
    public void nodeInvoked(Object node, String nodeName, String methodName, Object typedEvent) {
//        System.out.println("node invoked:" + node +  ", method:" + methodName + " event:" + typedEvent.getClass().getCanonicalName());
    }

    @Override
    public boolean auditInvocations() {
        return true;
    }
    
    public static class ProfilerMap{
        
    }
    
}
