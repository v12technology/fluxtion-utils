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

import static com.fluxtion.runtime.plugin.executor.SchedulingConfig.Strategy.BUSY;
import static com.fluxtion.runtime.plugin.executor.SchedulingConfig.Strategy.SLEEP;
import static com.fluxtion.runtime.plugin.executor.SchedulingConfig.Strategy.WAIT;

/**
 *
 * @author V12 Technology Limited
 */
public class SchedulingConfig {

    public enum Strategy {
        SLEEP, BUSY, WAIT
    }

    private final Strategy strategy;
    private final long sleep;

    public static SchedulingConfig busy() {
        return new SchedulingConfig(BUSY, 0);
    }

    public static SchedulingConfig sleep(long sleepInMicros) {
        return new SchedulingConfig(SLEEP, sleepInMicros);
    }

    public static SchedulingConfig waitForTask() {
        return new SchedulingConfig(WAIT, 0);
    }

    public static SchedulingConfig sleepInMillis(long sleepInMillis) {
        return sleep(sleepInMillis * 1000);
    }

    public static SchedulingConfig sleepInSeconds(long sleepSeconds) {
        return sleep(sleepSeconds * 1000);
    }

    private SchedulingConfig(Strategy strategy, long sleep) {
        this.strategy = strategy;
        this.sleep = sleep;

    }

    public Strategy getStrategy() {
        return strategy;
    }

    public long getSleep() {
        return sleep;
    }

}
