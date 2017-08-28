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
 * NumericSignal is an event that provides a generic method for nodes to receive
 * numeric values without having to define bespoke events for each type
 * of signal.
 *
 * NumericSignal has a filter string, which allows the receiver to filter
 * which NumericSignal it should be informed of. A node marks a method with a
 * filtered EventHandler annotation to receive a control message:
 *
 * <pre>
 *
 *{@literal @}EventHandler(filterString = "filterString", propagate = false)
 * public void numericUpdate(NumericSignal numericSignal){
 *
 * }
 * </pre>
 * 
 * Using the propagate=false will prevent a signal from starting an
 * event chain for any dependent nodes.
 *
 * The NumericSignal also provides an optional enable flag the
 * receiver can inspect.
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
