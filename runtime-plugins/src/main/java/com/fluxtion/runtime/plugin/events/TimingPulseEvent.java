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
package com.fluxtion.runtime.plugin.events;

import com.fluxtion.runtime.event.Event;

/**
 * An event notifying the SEP of a new wallclock time.
 *
 * @author greg
 */
public class TimingPulseEvent extends Event {

    public static final int ID = 245;

    public TimingPulseEvent(long currentTimeMillis) {
        super(ID);
        this.currentTimeMillis = currentTimeMillis;
    }

    public TimingPulseEvent() {
        super(ID);
        this.currentTimeMillis = -1;
    }

    /**
     * Override this value with number greater than zero
     */
    public long currentTimeMillis = -1;

    @Override
    public String toString() {
        return "TimingPulseEvent{" + "currentTimeMillis=" + currentTimeMillis + '}';
    }

}
