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

/**
 * Reads an {@link Event} ready for processing by a SEP. If no event is
 * available then the implementation should return a null value. The
 * {@link SepExecutor} will call this method and invoke a registered SEP with
 * the {@link Event} if not null. The {@link SepExecutor} will ensure that the
 * SEP is always invoked with a single thread.<p>
 *
 * A SEP is not allowed to hold a reference to the {@link Event} the EventSource
 * produces, allowing recycling of instances.
 *
 * @author V12 Technology Limited
 */
public interface EventSource {

    Event read();
}
