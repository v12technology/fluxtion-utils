/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.runtime.plugin.eventsource;

import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.plugin.events.TimingPulseEvent;
import com.fluxtion.runtime.plugin.executor.EventSource;

/**
 *
 * @author greg
 */
public class TimingEventSourcre implements EventSource{

    private final TimingPulseEvent pulse = new TimingPulseEvent();
    private long delayNanos = 1_000_000_000;
    private long previous;

    public TimingEventSourcre(long previous) {
        this.previous = previous;
    }

    public TimingEventSourcre() {
    }
    
    @Override
    public Event read() {
        long now = System.nanoTime();
        boolean firePulse = delayNanos < (now - previous);
        Event ret = null;
        if(firePulse){
            previous = now;
            ret = pulse;
        }
        return ret;
    }

    public long getDelayNanos() {
        return delayNanos;
    }

    public void setDelayNanos(long delayNanos) {
        this.delayNanos = delayNanos<1?this.delayNanos:delayNanos;
    }

    
}
