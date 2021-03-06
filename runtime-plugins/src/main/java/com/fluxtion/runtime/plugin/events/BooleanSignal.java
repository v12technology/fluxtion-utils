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
 * BooleanSignal, an event that facilitates publishing control signals to event
 * handlers. BooleanSignals remove the need to define bespoke control events by
 * using a named signal and filtering .
 * <br>
 *
 * The SEP uses the BooleanSignal name to filter the events a receiver will
 * process. The generated SEP provide all filtering logic within the generated
 * dispatch code. A node marks a method with a <b>filtered EventHandler</b> annotation
 * as shown:<br>
 * 
 * <pre>
 *{@literal @}EventHandler(filterString = "filterString")
 * public void controlMethod(BooleanSignal publishSignal){
 * }
 * </pre>
 *
 * Using the propagate=false will ensure the event is consumed by the signal
 * handler. Swallowing an event prevents a control signal from executing an
 * event chain for any dependent nodes of the event processor:
 * <br>
 * 
 * <pre>
 *{@literal @}EventHandler(filterString = "filterString", propagate = false)
 * </pre>
 *
 * The BooleanSignal also provides an optional enable flag the receiver can
 * inspect, accessed via {@link #isEnabled() }.
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class BooleanSignal extends Event {

    private final boolean value;

    public BooleanSignal(String signalName, boolean enabled) {
        super(NO_ID, signalName);
        this.value = enabled;
    }

    public boolean isEnabled() {
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
