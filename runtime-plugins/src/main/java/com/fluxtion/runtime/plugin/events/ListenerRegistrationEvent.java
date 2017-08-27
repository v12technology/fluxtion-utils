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
 * A generic listener registration event.
 * 
 * @author V12 Technology Limited
 * @param <T>
 */
public class ListenerRegistrationEvent<T> extends GenericEvent<T> {

    public final boolean register;
    
    public <V extends T> ListenerRegistrationEvent(Class<T> listenerClass, V value, boolean register) {
        super(listenerClass, value);
        this.register = register;
    }

}
