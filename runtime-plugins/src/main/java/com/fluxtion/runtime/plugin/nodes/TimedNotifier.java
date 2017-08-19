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
package com.fluxtion.runtime.plugin.nodes;

import com.fluxtion.runtime.plugin.events.TimingPulseEvent;
import com.fluxtion.api.annotations.AfterEvent;
import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.runtime.plugin.logging.EventLogSource;
import com.fluxtion.runtime.plugin.logging.EventLogger;

/**
 * Fires an update when a schedule period has expired.
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class TimedNotifier implements EventLogSource {

    //config variables
    private final int periodInSeconds;
    //private state
    private boolean fireUpdate = false;
    private int period;
    private long previous;
    private long timeInSeconds;
    private EventLogger log;

    public TimedNotifier(int periodInSeconds) {
        this.periodInSeconds = periodInSeconds;
    }

    public TimedNotifier() {
        this(0);
    }

    @EventHandler
    public boolean onTimingPulse(TimingPulseEvent pulse) {
        long millis = pulse.currentTimeMillis < 0 ? System.currentTimeMillis() : pulse.currentTimeMillis;
        timeInSeconds = (long) (millis * 0.001);
        fireUpdate = (timeInSeconds - previous) >= period;
        log.info("fireUpdate", fireUpdate);
        log.info("timeInSeconds", timeInSeconds);
        log.info("previous", previous);
        if (fireUpdate) {
            previous = previous + period * ((int) (timeInSeconds - previous) / period);
            while ((timeInSeconds - previous) >= period) {
                previous = previous + period;
            }
        }
        return fireUpdate;
    }

    public int getPeriod() {
        return periodInSeconds;
    }

    public long timeInSeconds() {
        return timeInSeconds;
    }

    /**
     * returns true when the period has expired, resets on the next run
     * TImingPulse update
     *
     * @return firing status
     */
    public boolean isFired() {
        return fireUpdate;
    }

    @AfterEvent
    public void resetFiredFlag() {
        fireUpdate = false;
    }

    /**
     * Copy config to private variables
     */
    @Initialise
    public void init() {
        period = periodInSeconds;
        previous = 0;
        timeInSeconds = 0;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + this.periodInSeconds;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TimedNotifier other = (TimedNotifier) obj;
        if (this.periodInSeconds != other.periodInSeconds) {
            return false;
        }
        return true;
    }

    @Override
    public void setLogger(EventLogger log) {
        this.log = log;
    }

}
