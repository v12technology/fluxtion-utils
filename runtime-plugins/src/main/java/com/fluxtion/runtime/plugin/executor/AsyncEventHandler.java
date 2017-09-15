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
package com.fluxtion.runtime.plugin.executor;

import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.lifecycle.EventHandler;
import java.util.concurrent.Future;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 * @param <E>
 */
public interface AsyncEventHandler<E extends EventHandler> extends EventHandler {

    public static AsyncEventHandler NULL_ASYNCEVENTHANDLER = new AsyncEventHandler() {

        @Override
        public Future submitTask(SepCallable task) {
            return null;
        }

        @Override
        public void onEvent(Event e) {
        }

        @Override
        public EventHandler delegate() {
            return null;
        }
        
        
    };
//
    EventHandler delegate();
//
//    <T> Future<T> submit(Callable<T> task);

    <T> Future<T> submitTask(SepCallable<T, E> task);

}
