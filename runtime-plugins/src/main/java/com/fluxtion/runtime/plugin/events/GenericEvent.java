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
 * A generic event, where the filter is determined by the class type. An event
 * handler can use the following syntax to receive events filtered by generic type
 * 
 * <pre>
 *{@literal @}EventHandler
 * public void someMethod(GenericEvent&lt;MyTYpe&gt; event){
 * ...
 * }
 * </pre>
 * 
 * @author Greg Higgins (greg.higgins@V12technology.com)
 * @param <T> The listener to register
 */
public class GenericEvent <T> extends Event{
    
    public final T value;

    public GenericEvent(T value){
        super(Event.NO_ID, value.getClass().getCanonicalName());
        this.value = value;
        
    }
    public <V extends T> GenericEvent(Class<T> valueClass, V value){
        super(Event.NO_ID, valueClass.getCanonicalName());
        this.value = value;
        
    }
    
}
