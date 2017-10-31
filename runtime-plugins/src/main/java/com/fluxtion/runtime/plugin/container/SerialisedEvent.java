/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.runtime.plugin.container;

import com.fluxtion.runtime.event.Event;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class SerialisedEvent {
    
    private Event event;
    private String eventClass;
    public static final String EVENT_FIELD = "event";
    public static final String EVENTCLASS_FIELD = "eventClass";

    public SerialisedEvent(Event event) {
        this.event = event;
        this.eventClass = event.getClass().getCanonicalName();
    }

    public SerialisedEvent() {
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
        this.eventClass = event.getClass().getCanonicalName();
    }

    public String getEventClass() {
        return eventClass;
    }

    public void setEventClass(String eventClass) {
        this.eventClass = eventClass;
    }
    
}
