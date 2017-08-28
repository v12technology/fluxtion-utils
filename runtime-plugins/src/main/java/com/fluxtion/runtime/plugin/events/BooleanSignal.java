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
package com.fluxtion.runtime.plugin.events;

import com.fluxtion.runtime.event.Event;

/**
 * BooleanSignal is an event that provides a generic method for nodes to receive
 control signals without having to define bespoke control events for each type
 of signal.
 *
 * The BooleanSignal has a filter string, which allows the receiver to filter
 which ControlSignals it should be informed of. A node marks a method with a
 filtered EventHandler annotation to receive a control message:

 <pre>

 EventHandler(filterString = "filterString", propogate = false)
 public void controlMethod(BooleanSignal publishSignal){

 }
 </pre>
 
 Using the propogate=false will prevent a control signal from starting an
 event chain for any dependent nodes.

 The BooleanSignal also provides an optional enable flag the
 receiver can inspect.
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class BooleanSignal extends Event {

    private final boolean value;

    public BooleanSignal(String signalName, boolean enabled) {
        super(NO_ID, signalName);
        this.value = enabled;
    }

    public boolean value() {
        return value;
    }

    @Override
    public String toString() {
        return "ControlSignal{"
                + "control.filter=" + filterString
                + "value=" + value
                + '}';
    }

}
