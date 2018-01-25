/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.runtime.plugin.events;

import com.fluxtion.runtime.event.Event;
import net.openhft.chronicle.wire.Marshallable;

/**
 *
 * @author gregp
 */
public abstract class ChronicleMarshallableEvent extends Event implements Marshallable {

    public ChronicleMarshallableEvent() {
    }

    public ChronicleMarshallableEvent(int id) {
        super(id);
    }

    public ChronicleMarshallableEvent(int id, int filterId) {
        super(id, filterId);
    }

    public ChronicleMarshallableEvent(int id, String filterString) {
        super(id, filterString);
    }

    public ChronicleMarshallableEvent(int id, int filterId, String filterString) {
        super(id, filterId, filterString);
    }

    @Override
    public boolean equals(Object o) {
        return Marshallable.$equals(this, o);
    }

    @Override
    public int hashCode() {
        return Marshallable.$hashCode(this);
    }

    @Override
    public String toString() {
        return Marshallable.$toString(this);
    }
}
