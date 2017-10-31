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
 *
 * NumericSignal, an event providing a generic method to publish numeric values
 * to event handlers. NumericSignal remove the need to define bespoke numeric
 * events for each type of signal by using a named signal.
 * <br>
 *
 * The NumericSignal name filters the events a receiver will process. The
 * generated SEP provide all filtering logic within the generated dispatch. A
 * node marks a method with a filtered EventHandler annotation as a receiving
 * method:
 *
 * <pre>
 *
 *{@literal @}EventHandler(filterString = "filterString")
 * public void numericUpdate(NumericSignal numericSignal){
 *
 * }
 * </pre>
 *
 * Using the propagate=false will ensure the event is consumed by the signal
 * handler. Swallowing an event prevents a control signal from executing an
 * event chain for any dependent nodes:
 * <br>
 *
 * <pre>
 *{@literal @}EventHandler(filterString = "filterString", propagate = false)
 * </pre>
 *
 * The receiver can use {@link #value()} method to access the value.
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class NumericSignal extends Event {

    private final double value;

    public NumericSignal(String signalName, double enabled) {
        super(NO_ID, signalName);
        this.value = enabled;
    }

    public double value() {
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
