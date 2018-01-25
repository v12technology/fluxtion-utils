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

/**
 * A generic listener registration event, extends {@link GenericEvent}. To
 * receive events filtered by type the event handler annotates a method similar
 * to that below:
 *
 *
 * <pre>
 *
 * private List&lt;AlarmListener&gt; listeners;
 *
 *{@literal @}EventHandler(propagate = false)
 * public void setAlarmListener(ListenerRegistrationEvent&lt;AlarmListener&gt; registration) {
 *   if(registration.register) {
 *     listeners.add(registration.listener());
 *   } else {
 *     listeners.remove(registration.listener());
 *   }
 * }
 * </pre>
 *
 * Using propagate=false will swallow the event at this node.
 * <p>
 * The generated SEP provide all filtering logic within the generated dispatch.
 *
 * @author V12 Technology Limited
 * @param <T> The listener type
 */
public class ListenerRegistrationEvent<T> extends GenericEvent<T> {

    public final boolean register;

    public ListenerRegistrationEvent(T listener, boolean register) {
        super(listener);
        this.register = register;
    }

    public <V extends T> ListenerRegistrationEvent(Class<T> listenerClass, V listener, boolean register) {
        super(listenerClass, listener);
        this.register = register;
    }

    public T listener() {
        return value;
    }
}
