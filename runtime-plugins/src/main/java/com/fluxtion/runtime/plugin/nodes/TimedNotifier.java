/* 
 *  Copyright (C) [2016]-[2017] V12 Technology Limited
 *  
 *  This software is subject to the terms and conditions of its EULA, defined in the
 *  file "LICENCE.txt" and distributed with this software. All information contained
 *  herein is, and remains the property of V12 Technology Limited and its licensors, 
 *  if any. This source code may be protected by patents and patents pending and is 
 *  also protected by trade secret and copyright law. Dissemination or reproduction 
 *  of this material is strictly forbidden unless prior written permission is 
 *  obtained from V12 Technology Limited.  
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
 * @author Greg Higgins
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
