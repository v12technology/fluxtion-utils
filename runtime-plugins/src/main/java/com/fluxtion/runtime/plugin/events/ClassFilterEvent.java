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
 * A generic listener registration/de-registration event. 
 * 
 * @author Greg Higgins (greg.higgins@V12technology.com)
 * @param <T> The listener to register
 */
public class ClassFilterEvent <T> extends Event{
    
    private final T listener;
    private final boolean register;

    public ClassFilterEvent(T listener, Class listenerClass) {
        super(Event.NO_ID, listenerClass.getCanonicalName());
        this.listener = listener;
        register = true;
    }

    public ClassFilterEvent(T listener, Class listenerClass, boolean register) {
        super(Event.NO_ID, listenerClass.getCanonicalName());
        this.listener = listener;
        this.register = register;
    }

    public T getListener() {
        return listener;
    }

    public boolean isRegister() {
        return register;
    }
    
}
